package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.LOG;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

@BotCommand
public class UpdateCommand {

	@Inject
	State state;

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
			String url = "https://github.com/Athou/GeekBot/commits/master.atom";
			SyndFeed rss = new SyndFeedInput().build(new StringReader(
					utilsService.getContent(url)));
			Date publishedDate = rss.getPublishedDate();
			Long newDate = publishedDate.getTime();
			Long oldDate = state.get(Long.class);
			if (oldDate != null && newDate > oldDate) {
				result = "New version detected, restarting... "
						+ utilsService
								.bitly("https://github.com/Athou/GeekBot/commits/master/");
				restart();
			}
			state.put(newDate);

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
