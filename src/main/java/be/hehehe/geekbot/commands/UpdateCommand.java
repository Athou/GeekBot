package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.LOG;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@BotCommand
public class UpdateCommand {

	private static String LATEST_VERSION;

	@Inject
	BotUtilsService utilsService;

	@Trigger(value = "!update")
	@Help("Update from GitHub and restarts the bot.")
	public String update() {
		restart();
		return "brb";
	}

	@TimedAction(1)
	public String checkIfNewVersionAvailable() {
		String result = null;
		try {
			URL url = new URL(
					"https://github.com/Athou/GeekBot/commits/master.atom");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new XmlReader(url));

			Collection<?> items = rss.getEntries();
			Iterator<?> it = items.iterator();
			SyndEntry item = (SyndEntry) it.next();
			String guid = item.getUri();
			if (LATEST_VERSION != null
					&& !StringUtils.equals(LATEST_VERSION, guid)) {
				result = "New version detected, restarting... "
						+ utilsService.bitly(item.getLink());
				restart();
			}
			LATEST_VERSION = guid;

		} catch (Exception e) {
			LOG.handle(e);
		}

		return result;
	}

	private void restart() {
		try {
			Runtime.getRuntime().exec("sh update.sh");
		} catch (Exception e) {
			LOG.handle(e);
		}
	}
}
