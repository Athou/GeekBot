package be.hehehe.geekbot.bot;

import java.util.List;

import org.apache.log4j.Logger;
import org.jibble.pircbot.PircBot;

public class IRCBot extends PircBot {

	private static Logger log = Logger.getLogger(IRCBot.class);

	private String botName;
	private List<String> channels;

	private MessageListener listener;

	public IRCBot(String server, int port, String botName, List<String> channels, MessageListener messageListener) {
		try {
			this.botName = botName;
			this.channels = channels;
			this.listener = messageListener;
			log.info("Connecting to " + server + " on port " + port);
			setMessageDelay(2000);
			setVersion("GeekBot - https://github.com/Athou/GeekBot");
			setName(botName);
			setLogin(botName);
			setVerbose(true);
			setAutoNickChange(true);
			setEncoding("UTF-8");
			setFinger(botName);
			connect(server, port);
			rejoin();
			log.info("Connected to " + server + " on port " + port);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		listener.onMessage(channel, sender, login, hostname, message);

	}

	@Override
	protected void onDisconnect() {
		while (!isConnected()) {
			try {
				Thread.sleep(10000);
				reconnect();
				rejoin();
			} catch (Exception e) {
				log.error("Could not reconnect", e);
			}
		}
	}

	private void rejoin() {
		for (String channel : channels) {
			joinChannel(channel);
		}
	}

	/**
	 * Try to rejoin channel when kicked
	 */
	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick,
			String reason) {
		if (recipientNick.equals(botName)) {
			this.joinChannel(channel);
		}
	}

	public interface MessageListener {
		public void onMessage(String channel, String sender, String login, String hostname, String message);
	}
}
