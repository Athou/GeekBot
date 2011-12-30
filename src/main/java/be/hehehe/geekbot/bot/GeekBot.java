package be.hehehe.geekbot.bot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.weld.environment.se.WeldContainer;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@Singleton
public class GeekBot extends PircBot {

	private String botName;

	private String channel;
	private List<Method> triggers;
	private List<Method> randoms;

	@Inject
	GeekBotCDIExtension extension;

	@Inject
	BundleService bundleService;

	@Inject
	BotUtilsService utilsService;

	@Inject
	WeldContainer container;

	@PostConstruct
	public void init() {

		botName = bundleService.getBotName();
		channel = bundleService.getChannel();
		String server = bundleService.getServer();
		try {

			// scan for commands
			triggers = extension.getTriggers();
			randoms = extension.getRandoms();
			startTimers(extension.getTimers());

			startChangeNickThread();

			// set parameters and connect to IRC
			this.setMessageDelay(2000);
			this.setName(botName);
			this.setLogin(botName);
			this.setVerbose(true);
			this.setAutoNickChange(true);
			this.setEncoding("ISO-8859-1");
			this.setFinger(botName);
			this.startIdentServer();
			this.connect(server);
			this.joinChannel(channel);

		} catch (NickAlreadyInUseException e) {
			LOG.error("Nick already in use !");
		} catch (IOException e) {
			LOG.handle(e);
		} catch (IrcException e) {
			LOG.handle(e);
		}
	}

	private void startTimers(List<Method> timers) {
		ScheduledExecutorService scheduler = Executors
				.newScheduledThreadPool(1);
		for (final Method method : timers) {
			int interval = method.getAnnotation(TimedAction.class).value();
			TimeUnit timeUnit = method.getAnnotation(TimedAction.class)
					.timeUnit();
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					try {
						invoke(method, buildEvent());
					} catch (Exception e) {
						LOG.handle(e);
					}
				}
			};
			scheduler.scheduleAtFixedRate(thread, 1,
					timeUnit.toMinutes(interval), TimeUnit.MINUTES);
		}
	}

	private void startChangeNickThread() {
		ScheduledExecutorService scheduler = Executors
				.newScheduledThreadPool(1);
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				if (!StringUtils.equals(botName, getNick())) {
					changeNick(botName);
				}
			}
		};
		scheduler.scheduleAtFixedRate(thread, 1, 1, TimeUnit.MINUTES);
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {

		if (message.equalsIgnoreCase("!help")) {
			printHelp();
		} else {
			handlePossibleTrigger(message, sender);
		}
	}

	@Override
	protected void onDisconnect() {
		try {
			while (!isConnected()) {
				this.reconnect();
				this.joinChannel(channel);
			}
		} catch (NickAlreadyInUseException e) {
			LOG.error("Nick already in use!");
		} catch (IOException e) {
			LOG.handle(e);
		} catch (IrcException e) {
			LOG.handle(e);
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
	 * Print available triggers
	 */
	private void printHelp() {
		List<String> result = new ArrayList<String>();
		result.add(IRCUtils.bold("Triggers: "));
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		for (Method m : triggers) {
			Trigger trigger = m.getAnnotation(Trigger.class);
			TriggerType type = trigger.type();
			if (type == TriggerType.EXACTMATCH
					|| type == TriggerType.STARTSWITH) {
				set.add(trigger.value().trim());
			}
		}
		result.add(StringUtils.join(set, " "));
		sendMessages(result);
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
			}

			invoke(triggerMethod, triggerEvent);
		}

		if (!isMessageTrigger(message)) {
			// this message is not a trigger, try to proc something randomly
			for (Method random : randoms) {
				int rand = new Random().nextInt(100) + 1;
				int probability = random.getAnnotation(RandomAction.class)
						.value();
				if (rand <= probability) {
					invoke(random, buildEvent(message, author, null));
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

	/**
	 * Invokes the trigger and handles its response.
	 * 
	 * @param method
	 * @param args
	 * @return
	 */
	private void invoke(final Method method, final TriggerEvent event) {
		if (event == null) {
			return;
		}

		LOG.debug("Invoking: " + method.getDeclaringClass().getSimpleName()
				+ "#" + method.getName());

		final Object commandInstance = container.instance()
				.select(method.getDeclaringClass()).get();

		ExecutorService executor = Executors.newCachedThreadPool();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Object result = null;
					if (method.getParameterTypes().length == 0) {
						result = method.invoke(commandInstance, new Object[0]);
					} else {
						result = method.invoke(commandInstance, event);
					}

					handleResultOfInvoke(result);
				} catch (Exception e) {
					LOG.handle(e);
				}
			}
		};
		executor.submit(runnable);

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
				isMessageTrigger(message));
		return event;
	}

	/**
	 * Handle the result of the trigger invokation
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void handleResultOfInvoke(Object o) {
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
		while (message.length() > 400) {
			int lastSpace = message.substring(0, 400).lastIndexOf(' ');
			sendMessage(channel, message.substring(0, lastSpace));
			message = message.substring(lastSpace + 1);
		}
		sendMessage(channel, message);
	}
}
