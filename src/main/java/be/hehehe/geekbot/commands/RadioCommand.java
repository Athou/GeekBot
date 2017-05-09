package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.persistence.dao.RSSFeedDAO;
import be.hehehe.geekbot.persistence.model.RSSFeed;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Checks Steam RSS news every minute
 * 
 */
@BotCommand
public class RadioCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@Inject
	Logger log;

	@SuppressWarnings("unchecked")
	@TimedAction(5)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<>();
		try {
			String url = "http://loul.hehehe.be/gmusic-feed";
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new StringReader(utilsService.getContent(url)));

			Iterator<SyndEntry> it = rss.getEntries().iterator();
			String message = null;
			while (it.hasNext()) {
				SyndEntry item = it.next();
				String guid = item.getUri();
				RSSFeed radio = dao.findByGUID(guid);
				if (radio == null) {
					message = IRCUtils.bold("Athou Radio!") + " " + StringEscapeUtils.unescapeXml(item.getTitle()) + " - "
							+ utilsService.bitly(item.getLink());
					toReturn.add(message);
					radio = new RSSFeed();
					radio.setGuid(guid);
					dao.save(radio);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return toReturn;
	}
}
