package be.hehehe.geekbot.bot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.persistence.lucene.ConnerieIndex;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@Singleton
public class GeekBot extends PircBot {

	private String botname;

	private String channel;
	private List<Method> triggers;
	private List<Method> randoms;

	@PostConstruct
	public void init() {
		LOG.info("test");
	}

	public GeekBot(String botName, String channel, String server) {
		try {

			triggers = ScannerHelper.scanTriggers();
			randoms = ScannerHelper.scanRandom();
			startTimers(ScannerHelper.scanTimers());
			ConnerieIndex.startRebuildingIndexThread();

			this.botname = botName;
			this.channel = channel;
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
		for (final Method method : timers) {
			int interval = method.getAnnotation(TimedAction.class).value();
			ScheduledExecutorService scheduler = Executors
					.newScheduledThreadPool(1);
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					try {
						handleResultOfInvoke(invoke(method, buildEvent()));
					} catch (Exception e) {
						LOG.handle(e);
					}
				}
			};
			scheduler.scheduleAtFixedRate(thread, interval * 60, interval * 60,
					TimeUnit.SECONDS);
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		// change nick everytime someone talks
		if (!getNick().equals(botname)) {
			changeNick(botname);
		}

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
		if (recipientNick.equals(botname)) {
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
		boolean triggered = false;

		for (Method triggerMethod : triggers) {
			Trigger trig = triggerMethod.getAnnotation(Trigger.class);
			String trigger = trig.value();
			String triggerStartsWith = trigger + " ";

			Object result = null;
			switch (trig.type()) {

			case EXACTMATCH:
				if (StringUtils.equals(trigger, message)) {
					TriggerEvent triggerEvent = buildEvent(message, author,
							trigger);
					result = invoke(triggerMethod, triggerEvent);
				}
				break;
			case STARTSWITH:
				if (StringUtils.startsWith(message, triggerStartsWith)) {
					TriggerEvent triggerEvent = buildEvent(message, author,
							triggerStartsWith);
					result = invoke(triggerMethod, triggerEvent);
				}
				break;
			case BOTNAME:
				if (botNameInMessage(message)) {
					TriggerEvent triggerEvent = buildEvent(message, author,
							botname);
					result = invoke(triggerMethod, triggerEvent);
				}
				break;

			case EVERYTHING:
				if (!isMessageTrigger(message) && !botNameInMessage(message)) {
					TriggerEvent triggerEvent = buildEvent(message, author,
							null);
					result = invoke(triggerMethod, triggerEvent);
				}
				break;
			}

			triggered |= handleResultOfInvoke(result);
		}

		// nothing triggered so far, try to proc a random action
		if (!triggered) {
			for (Method random : randoms) {
				int rand = new Random().nextInt(100) + 1;
				int probability = random.getAnnotation(RandomAction.class)
						.value();
				if (rand <= probability) {
					TriggerEvent triggerEvent = buildEvent(message, author,
							null);
					handleResultOfInvoke(invoke(random, triggerEvent));
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
		return StringUtils.containsIgnoreCase(message, botname);
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
	 * Invoke the trigger
	 * 
	 * @param method
	 * @param args
	 * @return
	 */
	private static Object invoke(Method method, TriggerEvent event) {
		Object result = null;
		try {
			LOG.debug("Invoking: " + method.getDeclaringClass().getSimpleName()
					+ "#" + method.getName());

			Object newInstance = method.getDeclaringClass().newInstance();
			if (method.getParameterTypes().length == 0) {
				result = method.invoke(newInstance, new Object[0]);
			} else {
				result = method.invoke(newInstance, event);
			}
		} catch (Exception e) {
			LOG.handle(e);
		}
		return result;
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
				users, nickInMessage(message), botNameInMessage(message));
		return event;
	}

	/**
	 * Handle the result of the trigger invokation
	 * 
	 */
	@SuppressWarnings("unchecked")
	private boolean handleResultOfInvoke(Object o) {
		boolean triggered = false;
		if (o != null) {
			if (o instanceof String) {
				String message = (String) o;
				triggered = StringUtils.isNotBlank(message);
				sendMessage(message);
			} else if (o instanceof List<?>) {
				List<String> messages = (List<String>) o;
				triggered = !messages.isEmpty();
				sendMessages(messages);
			}
		}
		return triggered;
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
