package be.hehehe.geekbot.commands;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

/**
 * Check twitch.tv feed status (configure feeds in twitch.properties)
 * 
 * 
 */
@BotCommand
public class TwitchCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	State state;

	@Inject
	Logger log;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		List<Stream> streams = state.get(List.class);
		if (streams == null) {
			streams = Lists.newArrayList();
			Properties props = new Properties();
			InputStream is = null;
			try {
				String configPath = "/twitch.properties";
				String openshiftDataDir = System.getenv("OPENSHIFT_DATA_DIR");
				if (openshiftDataDir != null) {
					is = new FileInputStream(openshiftDataDir + configPath);
				} else {
					is = getClass().getResourceAsStream(configPath);
				}
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
					alerts.add(IRCUtils.bold(stream.getName()) + " is now live on http://www.twitch.tv/" + stream.getName());
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
