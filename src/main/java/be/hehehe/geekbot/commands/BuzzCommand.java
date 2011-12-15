package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jsoup.Jsoup;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
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
public class BuzzCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@SuppressWarnings("unchecked")
	@Trigger("!buzz")
	public List<String> getLatestBuzz() {
		List<String> toReturn = new ArrayList<String>();
		try {
			URL url = new URL("http://www.jeanmarcmorandini.com/rss.php");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new XmlReader(url));

			Iterator<SyndEntry> it = rss.getEntries().iterator();
			String message = null;
			while (it.hasNext()) {
				SyndEntry item = it.next();
				String guid = item.getUri();
				RSSFeed buzz = dao.findByGUID(guid);
				if (buzz == null) {
					buzz = new RSSFeed();
					buzz.setGuid(item.getUri());
					dao.save(buzz);
					String urlBitly = utilsService.bitly(item.getLink());
					String content = Jsoup
							.parse(item.getDescription().getValue())
							.select("p").get(0).text();
					message = IRCUtils.bold("EXCLU!") + " " + item.getTitle()
							+ " - " + urlBitly;
					toReturn.add(message);
					toReturn.add(content);
					break;
				}
			}

			if (message == null) {
				toReturn.add("Pas d'exclus pour le moment.");
			}

		} catch (Exception e) {
			LOG.handle(e);
		}

		return toReturn;
	}
}
