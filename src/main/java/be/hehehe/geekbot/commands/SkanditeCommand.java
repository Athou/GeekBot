package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.SkanditeDAO;
import be.hehehe.geekbot.persistence.model.Skandite;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.HashAndByteCount;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class SkanditeCommand {

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> handleSkandites(TriggerEvent event) {

		List<String> result = new ArrayList<String>();
		String url = BotUtils.extractURL(event.getMessage());
		if (url != null) {
			SkanditeDAO dao = new SkanditeDAO();
			Skandite skandite = dao.findByURL(url);
			HashAndByteCount hashAndByteCount = null;
			if (skandite == null) {
				hashAndByteCount = BotUtils.calculateHashAndByteCount(url);
				if (hashAndByteCount != null) {
					skandite = dao.findByHashAndByteCount(
							hashAndByteCount.getHash(),
							hashAndByteCount.getByteCount());
				}
			}

			if (skandite != null) {
				String line = IRCUtils.bold("Skandite! ") + skandite.getUrl()
						+ " linked "
						+ BotUtils.getTimeDifference(skandite.getPostedDate())
						+ " ago by " + skandite.getAuthor() + " ("
						+ skandite.getCount() + "x).";
				result.add(line);
				skandite.setCount(skandite.getCount() + 1);
				dao.save(skandite);
			} else {
				skandite = new Skandite();
				skandite.setUrl(url);
				skandite.setPostedDate(new Date());
				skandite.setAuthor(event.getAuthor());
				skandite.setCount(1l);
				if (hashAndByteCount != null) {
					skandite.setHash(hashAndByteCount.getHash());
					skandite.setByteCount(hashAndByteCount.getByteCount());
				}
				dao.save(skandite);
			}

			if (url.contains("youtube.com") || url.contains("youtu.be")) {
				String videoParam = null;
				if (url.contains("youtube.com")) {
					videoParam = BotUtils.getRequestParametersFromURL(url).get(
							"v");
				} else {
					videoParam = BotUtils.extractIDFromYoutuDotBeURL(url);
				}
				String data = "http://gdata.youtube.com/feeds/api/videos/"
						+ videoParam;
				try {
					String content = BotUtils.getContent(data);
					Document doc = BotUtils.parseXML(content);
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
					LOG.handle(e);
				}
			}

			if (url.contains("vimeo.com")) {
				String videoParam = BotUtils.extractIDFromYoutuDotBeURL(url);
				String data = "http://vimeo.com/api/v2/video/" + videoParam
						+ ".json";
				try {
					String content = BotUtils.getContent(data);
					JSONArray array = new JSONArray(content);
					String title = array.getJSONObject(0).getString("title");
					String line = IRCUtils.bold("Vimeo") + " - " + title;
					result.add(line);
				} catch (Exception e) {
					LOG.handle(e);
				}
			}
		}
		return result;
	}
}
