package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
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
 * Polls for new blog posts on commabeat.com (French)
 * 
 */
@BotCommand
public class CommaBeatCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@Inject
	Logger log;

	@TimedAction(1)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<String>();
		try {
			String url = "http://feeds.feedburner.com/Commabeat";
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new StringReader(utilsService
					.getContent(url)));

			Collection<?> items = rss.getEntries();
			Iterator<?> it = items.iterator();
			String message = null;
			while (it.hasNext()) {
				Object o = it.next();
				SyndEntry item = (SyndEntry) o;
				String guid = item.getUri();
				RSSFeed beat = dao.findByGUID(guid);
				if (beat == null) {
					message = IRCUtils.bold("CommaBeat!") + " "
							+ StringEscapeUtils.unescapeXml(item.getTitle())
							+ " - " + utilsService.bitly(item.getLink());
					toReturn.add(message);
					beat = new RSSFeed();
					beat.setGuid(guid);
					dao.save(beat);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return toReturn;
	}
}
