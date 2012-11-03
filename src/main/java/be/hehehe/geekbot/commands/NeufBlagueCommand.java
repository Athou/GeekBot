package be.hehehe.geekbot.commands;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

@BotCommand
public class NeufBlagueCommand {

	@Inject
	Logger log;

	@Inject
	BotUtilsService utilsService;

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

			String content = new BotUtilsService().getContent(location);
			Document document = Jsoup.parse(content);
			String title = document.select("[property=og:title]").first()
					.attr("content");

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
}
