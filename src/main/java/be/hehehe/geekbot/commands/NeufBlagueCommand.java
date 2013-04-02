package be.hehehe.geekbot.commands;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

@BotCommand
public class NeufBlagueCommand {

	@Inject
	Logger log;

	@Inject
	BotUtilsService utilsService;

	@Trigger("!9blague")
	public String lol2() {
		return lol();
	}

	@Trigger("!9")
	public String lol() {
		String result = null;

		HttpURLConnection connection = null;
		InputStream is = null;
		try {
			URL url = new URL("http://9blague.com/random");
			connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.connect();
			String location = connection.getHeaderField("Location");

			String content = utilsService.getContent(location);
			Document document = Jsoup.parse(content);
			String title = document.select("[property=og:title]").first()
					.attr("content");

			Element imgElement = document.select(".img-wrap img").first();
			if (imgElement != null) {
				String imgSrc = imgElement.attr("src");
				String imgur = utilsService.mirrorImage(imgSrc);
				if (imgur != null) {
					location = imgur;
				}
			}

			result = IRCUtils.bold("9Blague ! ") + title + " " + " - "
					+ location;
		} catch (Exception e) {
			result = "Could not contact 9blague";
			log.error("Could not contact 9blague", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	@Trigger("!27")
	public void lolx3(TriggerEvent event) {
		for (int i = 0; i < 3; i++) {
			event.write(lol());
		}
	}
}
