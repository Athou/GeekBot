package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.persistence.dao.RSSFeedDAO;
import be.hehehe.geekbot.persistence.model.RSSFeed;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.DiscordUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * Checks Steam RSS news every minute
 * 
 */
@BotCommand
@JBossLog
public class SteamCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@SuppressWarnings("unchecked")
	@TimedAction(1)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<>();
		try {
			String url = "http://store.steampowered.com/feeds/news.xml";
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new StringReader(utilsService.getContent(url)));

			Iterator<SyndEntry> it = rss.getEntries().iterator();
			String message = null;
			while (it.hasNext()) {
				SyndEntry item = it.next();
				String guid = item.getUri();
				RSSFeed steam = dao.findByGUID(guid);
				if (steam == null) {
					message = DiscordUtils.bold("Steam!") + " " + StringEscapeUtils.unescapeXml(item.getTitle()) + " - "
							+ utilsService.bitly(item.getLink());
					toReturn.add(message);
					steam = new RSSFeed();
					steam.setGuid(guid);
					dao.save(steam);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return toReturn;
	}
}
