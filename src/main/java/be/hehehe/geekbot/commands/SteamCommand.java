package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.persistence.dao.RSSFeedDAO;
import be.hehehe.geekbot.persistence.model.RSSFeed;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

/**
 * Checks Steam RSS news every minute
 * 
 */
@BotCommand
public class SteamCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@Inject
	Logger log;

	@SuppressWarnings("unchecked")
	@TimedAction(1)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<String>();
		try {
			String url = "http://store.steampowered.com/feeds/news.xml";
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new StringReader(utilsService
					.getContent(url)));

			Iterator<SyndEntry> it = rss.getEntries().iterator();
			String message = null;
			while (it.hasNext()) {
				SyndEntry item = it.next();
				String guid = item.getUri();
				RSSFeed steam = dao.findByGUID(guid);
				if (steam == null) {
					message = IRCUtils.bold("Steam!") + " "
							+ StringEscapeUtils.unescapeXml(item.getTitle())
							+ " - " + utilsService.bitly(item.getLink());
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
