package be.hehehe.geekbot.commands;

import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.jdom.Document;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

import com.google.common.collect.Lists;

/**
 * Check own3d.tv feed status (configure feeds in own3d.properties)
 * 
 * 
 */
@BotCommand
public class Own3dCommand {

	private static final List<Stream> STREAMS = Lists.newArrayList();

	@Inject
	BotUtilsService utilsService;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("own3d");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			STREAMS.add(new Stream(key, value));
		}
	}

	@TimedAction(1)
	public List<String> updateStreams() {
		List<String> alerts = Lists.newArrayList();
		for (Stream stream : STREAMS) {
			try {
				boolean isNowLive = getStreamStatus(stream);
				if (!stream.isLive() && isNowLive) {
					alerts.add(IRCUtils.bold(stream.getName())
							+ " is now live on http://www.own3d.tv/live/"
							+ stream.getId());
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
		Document doc = utilsService.parseXML(utilsService.getContent(url));
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
