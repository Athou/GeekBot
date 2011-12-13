package be.hehehe.geekbot.bot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

public class GeekBot extends PircBot {

	private String botname;

	private String channel;
	private List<Method> triggers;
	private List<Method> randoms;

	public GeekBot(String botName, String channel, String server) {
		try {
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

			triggers = ScannerHelper.scanTriggers();
			randoms = ScannerHelper.scanRandom();
			startTimers(ScannerHelper.scanTimers());
			ConnerieIndex.startRebuildingIndexThread();

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
						handleResultOfInvoke(invoke(method, new Object[0]));
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
					result = invoke(triggerMethod, new Object[0]);
				}
				break;
			case STARTSWITH:
				if (StringUtils.startsWith(message, triggerStartsWith)) {
					result = invoke(triggerMethod,
							message.substring(triggerStartsWith.length()));
				}
				break;
			case BOTNAME:
				if (botNameInMessage(message)) {
					result = invoke(triggerMethod, message);
				}
				break;

			case EVERYTHING:
				if (!isMessageTrigger(message) && !botNameInMessage(message)) {
					result = invoke(triggerMethod, message, author,
							nickInMessage(message));
				}
				break;
			}

			if (result != null) {
				triggered = true;
				handleResultOfInvoke(result);
			}

		}

		// nothing triggered so far, try to proc a random action
		if (!triggered) {
			for (Method random : randoms) {
				int rand = new Random().nextInt(100) + 1;
				int probability = random.getAnnotation(RandomAction.class)
						.value();
				if (rand <= probability) {
					handleResultOfInvoke(invoke(random, message));
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
		return nickInMessage;
	}

	/**
	 * Invoke the trigger
	 * 
	 * @param method
	 * @param args
	 * @return
	 */
	private static Object invoke(Method method, Object... args) {
		Object result = null;
		try {
			LOG.debug("Invoking: " + method.getDeclaringClass().getSimpleName()
					+ "#" + method.getName());
			result = method.invoke(method.getDeclaringClass().newInstance(),
					args);
		} catch (Exception e) {
			LOG.handle(e);
		}
		return result;
	}

	/**
	 * Handle the result of the trigger invokation
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void handleResultOfInvoke(Object o) {
		if (o != null) {
			if (o instanceof String) {
				sendMessage((String) o);
			} else if (o instanceof List<?>) {
				sendMessages((List<String>) o);
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
