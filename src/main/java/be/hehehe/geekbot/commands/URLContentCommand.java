package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * 
 * Analyze links
 * 
 */
@BotCommand
public class URLContentCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> handleLinks(TriggerEvent event) {
		List<String> result = new ArrayList<String>();

		if (event.hasURL()) {
			String url = event.getURL();
			// youtube
			if (url.contains("youtube.com") || url.contains("youtu.be")) {
				String videoParam = null;
				if (url.contains("youtube.com")) {
					videoParam = utilsService.getRequestParametersFromURL(url)
							.get("v");
				} else {
					videoParam = utilsService.extractIDFromYoutuDotBeURL(url);
				}
				String data = "http://gdata.youtube.com/feeds/api/videos/"
						+ videoParam;
				try {
					String content = utilsService.getContent(data);
					Document doc = utilsService.parseXML(content);
					Element root = doc.getRootElement();
					String title = "";
					for (Object o : root.getChildren()) {
						Element e = (Element) o;
						if ("title".equals(e.getName())) {
							title = e.getText();
							break;
						}
					}
					String line = IRCUtils.bold("Youtube") + " - " + title;
					result.add(line);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			// vimeo
			else if (url.contains("vimeo.com")) {
				String videoParam = utilsService
						.extractIDFromYoutuDotBeURL(url);
				String data = "http://vimeo.com/api/v2/video/" + videoParam
						+ ".json";
				try {
					String content = utilsService.getContent(data);
					JSONArray array = new JSONArray(content);
					String title = array.getJSONObject(0).getString("title");
					String line = IRCUtils.bold("Vimeo") + " - " + title;
					result.add(line);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			// twitter

			else if (url.contains("twitter.com")
					&& (url.contains("/status/") || url.contains("/statuses/"))) {

				String[] split = url.split("/");
				String statusId = null;
				for (int i = 0; i < split.length; i++) {
					String token = split[i];
					if (("status".equals(token) || "statuses".equals(token))
							&& i + 1 < split.length) {
						statusId = split[i + 1];
						break;
					}
				}
				if (statusId != null) {
					String data = "https://api.twitter.com/1/statuses/show.json?id="
							+ statusId + "&include_entities=true";
					String content = utilsService.getContent(data);
					String name = null;
					String text = null;

					try {
						JSONObject root = new JSONObject(content);
						name = root.getJSONObject("user").getString("name");
						text = root.getString("text");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
					String line = IRCUtils.bold(name + ": ") + text;
					result.add(line);

				}
			} else {
				String html = utilsService.getContent(url);
				try {
					String title = Jsoup.parse(html).select("head > title")
							.first().text();
					result.add(IRCUtils.bold("Title: ") + title);
				} catch (Exception e) {
					log.debug("Could not fetch HTML title: " + url);
				}
			}
		}
		return result;
	}
}
