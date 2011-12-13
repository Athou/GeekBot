package be.hehehe.geekbot.commands;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class GoogleCommand {

	@Trigger(value = "!google", type = TriggerType.STARTSWITH)
	public List<String> google(String keywords) {
		return getResult(keywords, "en", "web");
	}

	@Trigger(value = "!googlefr", type = TriggerType.STARTSWITH)
	public List<String> googlefr(String keywords) {
		return getResult(keywords, "fr", "web");
	}

	@Trigger(value = "!image", type = TriggerType.STARTSWITH)
	public List<String> image(String keywords) {
		return getResult(keywords, "en", "images");
	}

	@Trigger(value = "!imagefr", type = TriggerType.STARTSWITH)
	public List<String> imagefr(String keywords) {
		return getResult(keywords, "fr", "images");
	}

	public List<String> getResult(String keywords, String lang, String mode) {
		String result = "";
		try {
			String GOOGLE_API_URL = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=off&lr=lang_"
					+ lang + "&q=";
			String GOOGLE_API_IMAGES = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&safe=off&lr=lang_"
					+ lang + "&q=";
			String url;
			if ("web".equals(mode)) {
				url = GOOGLE_API_URL + URLEncoder.encode(keywords, "UTF-8");
			} else {
				url = GOOGLE_API_IMAGES + URLEncoder.encode(keywords, "UTF-8");
			}
			result = BotUtils.getContent(url);
		} catch (Exception e) {
			LOG.handle(e);
		}
		return parse(result, keywords, mode);
	}

	private List<String> parse(String source, String keywords, String mode) {
		List<String> result = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(source);

			JSONArray ja = json.getJSONObject("responseData").getJSONArray(
					"results");
			JSONObject j = ja.getJSONObject(0);
			if ("web".equals(mode)) {
				String firstLine = StringEscapeUtils.unescapeHtml(IRCUtils
						.bold(j.getString("titleNoFormatting")))
						+ " - "
						+ URLDecoder.decode(j.getString("url"), "UTF-8");

				result.add(firstLine);

				String content = j.getString("content");
				content = content.replaceAll("<b>", "");
				content = content.replaceAll("</b>", "");
				content = content.replaceAll("  ", "");

				content = StringEscapeUtils.unescapeHtml(content);

				content = content.replaceAll("&quot;", "\"");

				result.add(content);
			} else {
				String s = json.getJSONObject("responseData")
						.getJSONArray("results").getJSONObject(0)
						.getString("unescapedUrl");
				result.add(IRCUtils.bold(s));
			}

		} catch (Exception e) {
			result.add("Your search - " + keywords
					+ " - did not match any documents.");
		}

		return result;
	}

}
