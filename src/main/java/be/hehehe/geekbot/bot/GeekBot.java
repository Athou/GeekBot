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
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.annotations.Triggers;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;

import com.google.common.collect.Maps;

@Singleton
public class GeekBot extends PircBot {

	private String botName;

	private String channel;
	private List<Method> triggers;
	private List<Method> randoms;

	@Inject
	GeekBotCDIExtension extension;

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
		channel = bundleService.getChannel();

		String server = bundleService.getServer();
		int port = bundleService.getPort();
		boolean connect = !bundleService.isTest();
		try {

			// scan for commands
			triggers = extension.getTriggers();
			randoms = extension.getRandoms();
			setTimers(extension.getTimers());

			// set parameters and connect to IRC
			this.setMessageDelay(2000);
			this.setVersion("GeekBot - https://github.com/Athou/GeekBot");
			this.setName(botName);
			this.setLogin(botName);
			this.setVerbose(true);
			this.setAutoNickChange(true);
			this.setEncoding("ISO-8859-1");
			this.setFinger(botName);
			if (connect) {
				log.info("Connecting to " + server + " on port " + port);
				this.connect(server, port);
				this.joinChannel(channel);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@PreDestroy
	public void destroy() {
		sendMessage("brb, reboot");
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
		for (final Method method : timers.keySet()) {
			int interval = method.getAnnotation(TimedAction.class).value();
			TimeUnit timeUnit = method.getAnnotation(TimedAction.class)
					.timeUnit();

			if (now - timers.get(method) > timeUnit.toMillis(interval)) {
				invokeTrigger(method);
			}

		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {

		handlePossibleTrigger(message, sender);
	}

	@Override
	protected void onDisconnect() {
		while (!isConnected()) {
			try {
				Thread.sleep(10000);
				this.reconnect();
				this.joinChannel(channel);
			} catch (Exception e) {
				log.error("Could not reconnect", e);
			}
		}
	}

	/**
	 * Try to rejoin channel when kicked
	 */
	@Override
	protected void onKick(String channel, String kickerNick,
			String kickerLogin, String kickerHostname, String recipientNick,
			String reason) {
		if (recipientNick.equals(botName)) {
			this.joinChannel(channel);
		}
	}

	/**
	 * Handle each message and check for an action
	 * 
	 * @param message
	 * @param author
	 */
	private void handlePossibleTrigger(String message, String author) {

		for (Method triggerMethod : triggers) {
			Trigger trig = triggerMethod.getAnnotation(Trigger.class);
			String trigger = trig.value();
			String triggerStartsWith = trigger + " ";

			TriggerEvent triggerEvent = null;
			switch (trig.type()) {

			case EXACTMATCH:
				if (StringUtils.equals(trigger, message)) {
					triggerEvent = buildEvent(message, author, trigger);
				}
				break;
			case STARTSWITH:
				if (StringUtils.startsWith(message, triggerStartsWith)) {
					triggerEvent = buildEvent(message, author,
							triggerStartsWith);
				}
				break;
			case BOTNAME:
				if (botNameInMessage(message)) {
					triggerEvent = buildEvent(message, author, botName);
				}
				break;

			case EVERYTHING:
				if (!isMessageTrigger(message) && !botNameInMessage(message)) {
					triggerEvent = buildEvent(message, author, null);
				}
				break;
			default:
				break;
			}

			invokeTrigger(triggerMethod, triggerEvent);
		}

		if (!isMessageTrigger(message)) {
			// this message is not a trigger, try to proc something randomly
			for (Method random : randoms) {
				int rand = new Random().nextInt(100) + 1;
				int probability = random.getAnnotation(RandomAction.class)
						.value();
				if (rand <= probability) {
					invokeTrigger(random, buildEvent(message, author, null));
					break;
				}
			}
		}

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
			if (type == TriggerType.EXACTMATCH
					|| type == TriggerType.STARTSWITH) {
				if (StringUtils.startsWithIgnoreCase(message, trigger)) {
					isTrigger = true;
					break;
				}
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
	private boolean nickInMessage(String message) {
		boolean nickInMessage = false;
		if (message != null) {
			String[] split = message.split(" ");
			for (User user : getUsers(channel)) {
				for (String token : split) {
					if (token.equalsIgnoreCase(user.getNick())
							|| token.equalsIgnoreCase(user.getNick() + ":")) {
						nickInMessage = true;
						break;
					}
				}
			}
		}
		return nickInMessage;
	}

	public void invokeTrigger(final Method method) {
		invokeTrigger(method, buildEvent());
	}

	private void invokeTrigger(final Method method, final TriggerEvent event) {
		if (event == null) {
			return;
		}
		Future<?> future = invoker.invoke(method, event);

		try {
			Object result = future.get(1, TimeUnit.MINUTES);
			handleResultOfInvoke(result);
		} catch (Exception e) {
			log.error(
					"Error while invoking method "
							+ method.getDeclaringClass().getSimpleName() + "#"
							+ method.getName() + ": " + e.getMessage(), e);
		}

	}

	private TriggerEvent buildEvent() {
		return buildEvent(null, null, null);
	}

	@SuppressWarnings("unchecked")
	private TriggerEvent buildEvent(String message, String author,
			String trigger) {
		Collection<String> users = CollectionUtils.collect(
				Arrays.asList(getUsers(channel)),
				new BeanToPropertyValueTransformer("nick"));
		TriggerEvent event = new TriggerEventImpl(message, author, trigger,
				users, utilsService.extractURL(message),
				nickInMessage(message), botNameInMessage(message),
				isMessageTrigger(message), new MessageWriter() {
					@Override
					public void write(String message) {
						sendMessage(message);
					}
				}, scheduler);
		return event;
	}

	/**
	 * Handle the result of the trigger invokation
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void handleResultOfInvoke(Object o) {
		if (o != null) {
			if (o instanceof String) {
				String message = (String) o;
				sendMessage(message);
			} else if (o instanceof List<?>) {
				List<String> messages = (List<String>) o;
				sendMessages(messages);
			}
		}
	}

	private void sendMessages(List<String> list) {
		for (String s : list) {
			sendMessage(s);
		}
	}

	/**
	 * Sends the message, splitting it if too long.
	 * 
	 * @param message
	 */
	private void sendMessage(String message) {
		int limit = 400;
		while (message.length() > limit) {
			int lastSpace = message.substring(0, limit).lastIndexOf(' ');
			sendMessage(channel, message.substring(0, lastSpace));
			message = message.substring(lastSpace + 1);
		}
		sendMessage(channel, message);
	}

	@Produces
	@Triggers
	public List<Method> getTriggers() {
		return triggers;
	}

}
