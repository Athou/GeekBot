package be.hehehe.geekbot.commands;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * Check twitch.tv feed status (configure feeds in twitch.properties)
 * 
 * 
 */
@BotCommand
@JBossLog
public class TwitchCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	State state;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		List<Stream> streams = state.get(List.class);
		if (streams == null) {
			streams = Lists.newArrayList();
			Properties props = new Properties();
			InputStream is = null;
			try {
				String configPath = "twitch.properties";
				is = getClass().getResourceAsStream(configPath);
				props.load(is);
			} catch (Exception e) {
				log.fatal("Could not load config file");
			} finally {
				IOUtils.closeQuietly(is);
			}
			Set<Object> keys = props.keySet();
			for (Object keyObj : keys) {
				String key = (String) keyObj;
				streams.add(new Stream(key));
			}
			state.put(streams);
		}
	}

	@TimedAction(1)
	@SuppressWarnings("unchecked")
	public List<String> updateStreams() {
		List<String> alerts = Lists.newArrayList();
		List<Stream> streams = state.get(List.class);
		for (Stream stream : streams) {
			try {
				boolean isNowLive = getStreamStatus(stream);
				if (!stream.isLive() && isNowLive) {
					alerts.add(
							IRCUtils.bold(stream.getName().replace("_", "")) + " is now live on http://www.twitch.tv/" + stream.getName());
				}
				stream.setLive(isNowLive);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return alerts;
	}

	private boolean getStreamStatus(Stream stream) throws Exception {
		String url = "https://api.twitch.tv/kraken/streams/" + stream.getName();
		String jsonString = utilsService.getContent(url);
		boolean offline = new JSONObject(jsonString).isNull("stream");
		return !offline;
	}

	private static class Stream {
		private String name;
		private boolean live;

		public Stream(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public boolean isLive() {
			return live;
		}

		public void setLive(boolean live) {
			this.live = live;
		}

	}
}
