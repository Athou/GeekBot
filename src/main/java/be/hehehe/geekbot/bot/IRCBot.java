package be.hehehe.geekbot.bot;

import org.apache.log4j.Logger;
import org.jibble.pircbot.PircBot;

public class IRCBot extends PircBot {

	private static Logger log = Logger.getLogger(IRCBot.class);

	private String botName;
	private String channel;

	private MessageListener listener;

	public IRCBot(String server, int port, String botName, String channel,
			MessageListener messageListener) {
		try {
			this.botName = botName;
			this.channel = channel;
			this.listener = messageListener;
			log.info("Connecting to " + server + " on port " + port);
			setMessageDelay(2000);
			setVersion("GeekBot - https://github.com/Athou/GeekBot");
			setName(botName);
			setLogin(botName);
			setVerbose(true);
			setAutoNickChange(true);
			setEncoding("ISO-8859-1");
			setFinger(botName);
			connect(server, port);
			joinChannel(channel);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		listener.onMessage(channel, sender, login, hostname, message);

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

	public interface MessageListener {
		public void onMessage(String channel, String sender, String login,
				String hostname, String message);
	}
}
