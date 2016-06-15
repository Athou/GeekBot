package be.hehehe.geekbot.bot;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jibble.pircbot.User;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.annotations.Triggers;
import be.hehehe.geekbot.bot.IRCBot.MessageListener;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;

@Singleton
public class GeekBot {

	private static final List<Class<?>> ALL = Lists.newArrayList();

	private String botName;
	private List<String> channels;
	private Map<String, List<Class<?>>> channelCommands;

	private List<Method> triggers;
	private List<Method> randoms;

	@Inject
	GeekBotCDIExtension extension;

	private IRCBot bot;

	@Inject
	CommandInvoker invoker;

	@Inject
	BundleService bundleService;

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Inject
	Instance<Object> container;

	private ScheduledExecutorService scheduler;
	private Map<Method, Long> timers = Maps.newConcurrentMap();

	@PostConstruct
	public void init() {

		scheduler = Executors.newScheduledThreadPool(50);

		botName = bundleService.getBotName();
		channels = bundleService.getChannels();
		channelCommands = Maps.newHashMap();

		try {
			for (String channel : channels) {
				String property = bundleService.getValue("channel.command." + channel.replace("#", ""));
				if ("ALL".equals(property)) {
					channelCommands.put(channel.toUpperCase(), ALL);
				} else {
					List<Class<?>> list = Lists.newArrayList();
					String[] classes = property.split(",");
					for (String className : classes) {
						list.add(Class.forName(className));
					}
					channelCommands.put(channel.toUpperCase(), list);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		triggers = extension.getTriggers();
		randoms = extension.getRandoms();
		setTimers(extension.getTimers());

		String server = bundleService.getServer();
		int port = bundleService.getPort();
		boolean connect = !bundleService.isTest();

		final String discordBotName = bundleService.getDiscordBotName();

		if (connect) {
			bot = new IRCBot(server, port, botName, channels, new MessageListener() {

				@Override
				public void onMessage(String channel, String sender, String login, String hostname, String message) {
					if (StringUtils.equals(sender, discordBotName)) {
						int start = message.indexOf('<');
						int end = message.indexOf('>');

						sender = message.substring(start + 1, end);
						message = message.substring(end + 2);
					}
					handlePossibleTrigger(channel, message, sender);
				}
			});
		}
	}

	@PreDestroy
	public void destroy() {
		bot.quitServer("brb, reboot");
	}

	public void setTimers(List<Method> methods) {
		Long now = System.currentTimeMillis();
		for (Method method : methods) {
			timers.put(method, now);
		}
	}

	@Schedule(hour = "*", minute = "*", persistent = false)
	public void timer() {
		Long now = System.currentTimeMillis();
		for (String channel : channels) {
			for (final Method method : timers.keySet()) {
				if (isMethodAllowedToRun(channel, method)) {
					int interval = method.getAnnotation(TimedAction.class).value();
					TimeUnit timeUnit = method.getAnnotation(TimedAction.class).timeUnit();

					if (now - timers.get(method) > timeUnit.toMillis(interval)) {
						timers.put(method, now);
						invokeTrigger(channel, method);
					}
				}
			}
		}
	}

	/**
	 * Handle each message and check for an action
	 * 
	 * @param message
	 * @param author
	 */
	private void handlePossibleTrigger(String channel, String message, String author) {

		for (Method triggerMethod : triggers) {

			if (isMethodAllowedToRun(channel, triggerMethod)) {

				Trigger trig = triggerMethod.getAnnotation(Trigger.class);
				String trigger = trig.value();
				String triggerStartsWith = trigger + " ";

				TriggerEvent triggerEvent = null;
				switch (trig.type()) {

				case EXACTMATCH:
					if (StringUtils.equals(trigger, message)) {
						triggerEvent = buildEvent(channel, message, author, trigger);
					}
					break;
				case STARTSWITH:
					if (StringUtils.startsWith(message, triggerStartsWith)) {
						triggerEvent = buildEvent(channel, message, author, triggerStartsWith);
					}
					break;
				case BOTNAME:
					if (botNameInMessage(message)) {
						triggerEvent = buildEvent(channel, message, author, botName);
					}
					break;

				case EVERYTHING:
					if (!isMessageTrigger(message) && !botNameInMessage(message)) {
						triggerEvent = buildEvent(channel, message, author, null);
					}
					break;
				default:
					break;
				}

				invokeTrigger(channel, triggerMethod, triggerEvent);
			}
		}

		if (!isMessageTrigger(message)) {
			// this message is not a trigger, try to proc something randomly
			for (Method randomMethod : randoms) {
				if (isMethodAllowedToRun(channel, randomMethod)) {
					int rand = new Random().nextInt(100) + 1;
					int probability = randomMethod.getAnnotation(RandomAction.class).value();
					if (rand <= probability) {
						invokeTrigger(channel, randomMethod, buildEvent(channel, message, author, null));
						break;
					}
				}
			}
		}

	}

	private boolean isMethodAllowedToRun(String channel, Method method) {
		List<Class<?>> commands = channelCommands.get(channel.toUpperCase());
		return commands != null && (commands == ALL || commands.contains(method.getDeclaringClass()));
	}

	/**
	 * 
	 * @param message
	 * @return true if the message is a trigger
	 */
	private boolean isMessageTrigger(String message) {
		boolean isTrigger = false;
		for (Method triggerMethod : triggers) {
			Trigger trig = triggerMethod.getAnnotation(Trigger.class);
			String trigger = trig.value();
			TriggerType type = trig.type();
			if (type == TriggerType.EXACTMATCH || type == TriggerType.STARTSWITH) {
				if (StringUtils.startsWithIgnoreCase(message, trigger)) {
					isTrigger = true;
					break;
				}
			} else if (type == TriggerType.BOTNAME && botNameInMessage(message)) {
				isTrigger = true;
				break;
			}
		}
		return isTrigger;
	}

	/**
	 * 
	 * @param message
	 * @return true if the name of the bot is in the message
	 */
	private boolean botNameInMessage(String message) {
		return StringUtils.containsIgnoreCase(message, botName);
	}

	/**
	 * 
	 * @param message
	 * @return true if the msssage contains the nickname of a user on the chan
	 */
	private boolean nickInMessage(String channel, String message) {
		boolean nickInMessage = false;
		if (message != null) {
			String[] split = message.split(" ");
			for (User user : bot.getUsers(channel)) {
				for (String token : split) {
					if (token.equalsIgnoreCase(user.getNick()) || token.equalsIgnoreCase(user.getNick() + ":")) {
						nickInMessage = true;
						break;
					}
				}
			}
		}
		return nickInMessage;
	}

	public void invokeTrigger(String channel, final Method method) {
		invokeTrigger(channel, method, buildEvent(channel));
	}

	private void invokeTrigger(String channel, final Method method, final TriggerEvent event) {
		if (event == null) {
			return;
		}
		Future<?> future = invoker.invoke(method, event);

		try {
			Object result = future.get(1, TimeUnit.MINUTES);
			handleResultOfInvoke(channel, result);
		} catch (Exception e) {
			log.error("Error while invoking method " + method.getDeclaringClass().getSimpleName() + "#" + method.getName() + ": "
					+ e.getMessage(), e);
		}

	}

	private TriggerEvent buildEvent(String channel) {
		return buildEvent(channel, null, null, null);
	}

	@SuppressWarnings("unchecked")
	private TriggerEvent buildEvent(final String channel, String message, String author, String trigger) {
		Collection<String> users = CollectionUtils.collect(Arrays.asList(bot.getUsers(channel)),
				new BeanToPropertyValueTransformer("nick"));
		TriggerEvent event = new TriggerEventImpl(message, author, trigger, users, utilsService.extractURL(message),
				nickInMessage(channel, message), botNameInMessage(message), isMessageTrigger(message), new MessageWriter() {
					@Override
					public void write(String message) {
						sendMessage(channel, message);
					}
				}, scheduler);
		return event;
	}

	/**
	 * Handle the result of the trigger invokation
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void handleResultOfInvoke(String channel, Object o) {
		if (o != null) {
			if (o instanceof String) {
				String message = (String) o;
				sendMessage(channel, message);
			} else if (o instanceof List<?>) {
				List<String> messages = (List<String>) o;
				sendMessages(channel, messages);
			} else {
				sendMessage(channel, o.toString());
			}
		}
	}

	private void sendMessages(String channel, List<String> list) {
		for (String s : list) {
			sendMessage(channel, s);
		}
	}

	/**
	 * Sends the message, splitting it if too long.
	 * 
	 * @param message
	 */
	private void sendMessage(String channel, String message) {
		int limit = 400;
		while (message.length() > limit) {
			int lastSpace = message.substring(0, limit).lastIndexOf(' ');
			bot.sendMessage(channel, message.substring(0, lastSpace));
			message = message.substring(lastSpace + 1);
		}
		bot.sendMessage(channel, message);
	}

	@Produces
	@Triggers
	public List<Method> getTriggers() {
		return triggers;
	}

}
