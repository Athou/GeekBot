package be.hehehe.geekbot.commands;

import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.jdom.Document;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

import com.google.common.collect.Lists;

@BotCommand
public class Own3dCommand {

	private static final List<Stream> STREAMS = Lists.newArrayList();

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("own3d");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			STREAMS.add(new Stream(key, value));
		}
	}

	@TimedAction(value = 1)
	public List<String> getLatestPost() {
		List<String> alerts = Lists.newArrayList();
		for (Stream stream : STREAMS) {
			try {
				boolean isNowLive = getStreamStatus(stream);
				if (!stream.isLive() && isNowLive) {
					alerts.add(IRCUtils.bold(stream.getName())
							+ " is now live on http://www.own3d.tv/"
							+ stream.getName());
				}
				stream.setLive(isNowLive);
			} catch (Exception e) {
				LOG.handle(e);
			}
		}

		return alerts;
	}

	private boolean getStreamStatus(Stream stream) throws Exception {
		String url = "http://api.own3d.tv/liveCheck.php?live_id="
				+ stream.getId();
		Document doc = BotUtils.parseXML(BotUtils.getContent(url));
		String isLive = doc.getRootElement().getChild("liveEvent")
				.getChild("isLive").getText();
		return Boolean.parseBoolean(isLive);
	}

	private static class Stream {
		private String name;
		private String id;
		private boolean live;

		public Stream(String name, String id) {
			super();
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public boolean isLive() {
			return live;
		}

		public void setLive(boolean live) {
			this.live = live;
		}

	}
}
