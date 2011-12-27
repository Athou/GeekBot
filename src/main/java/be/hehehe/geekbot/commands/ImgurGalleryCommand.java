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

/**
 * Polls for new images on Imgur
 * 
 */
@BotCommand
public class ImgurGalleryCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	RSSFeedDAO dao;

	@TimedAction(1)
	public List<String> getLatestImages() {
		List<String> toReturn = new ArrayList<String>();
		try {
			URL url = new URL("http://feeds.feedburner.com/ImgurGallery");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed rss = input.build(new XmlReader(url));

			Collection<?> items = rss.getEntries();
			Iterator<?> it = items.iterator();
			while (it.hasNext()) {
				SyndEntry item = (SyndEntry) it.next();
				String guid = item.getUri();
				RSSFeed image = dao.findByGUID(guid);
				if (image == null) {
					String message = IRCUtils.bold(StringEscapeUtils
							.unescapeXml(item.getTitle()))
							+ " - "
							+ utilsService.bitly(item.getLink());
					toReturn.add(message);
					image = new RSSFeed();
					image.setGuid(guid);
					dao.save(image);
				}
			}

		} catch (Exception e) {
			LOG.handle(e);
		}

		return toReturn;
	}
}
