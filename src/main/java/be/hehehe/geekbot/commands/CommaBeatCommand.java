package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.persistence.dao.RSSFeedDAO;
import be.hehehe.geekbot.persistence.model.RSSFeed;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@BotCommand
public class CommaBeatCommand {

	@Inject
	BotUtilsService utilsService;
	
	@Inject
	RSSFeedDAO dao;

	@TimedAction(value = 1)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<String>();
		try {
			URL url = new URL("http://feeds.feedburner.com/Commabeat");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new XmlReader(url));

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
			LOG.handle(e);
		}

		return toReturn;
	}
}
