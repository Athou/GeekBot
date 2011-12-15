package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.ArrayList;
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
public class SteamCommand {

	@Inject
	private BotUtilsService utilsService;

	@Inject
	private RSSFeedDAO dao;

	@SuppressWarnings("unchecked")
	@TimedAction(value = 1)
	public List<String> getLatestPost() {
		List<String> toReturn = new ArrayList<String>();
		try {
			URL url = new URL("http://store.steampowered.com/feeds/news.xml");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new XmlReader(url));

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
			LOG.handle(e);
		}

		return toReturn;
	}
}
