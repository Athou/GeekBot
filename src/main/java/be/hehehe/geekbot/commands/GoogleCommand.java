package be.hehehe.geekbot.commands;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Google search, web or images
 * 
 */
@BotCommand
public class GoogleCommand {

	@Inject
	BotUtilsService utilsService;
	
	@Inject
	Logger log;

	public enum Lang {
		FRENCH("fr"), ENGLISH("en");

		private Lang(String lang) {
			this.lang = lang;
		}

		private String lang;

		@Override
		public String toString() {
			return lang;
		}
	}

	public enum Mode {
		WEB("web"), IMAGE("images");

		private Mode(String mode) {
			this.mode = mode;
		}

		private String mode;

		@Override
		public String toString() {
			return mode;
		}
	}

	@Trigger(value = "!google", type = TriggerType.STARTSWITH)
	@Help("Google search.")
	public List<String> google(TriggerEvent event) {
		return google(event.getMessage(), Lang.ENGLISH, Mode.WEB);
	}

	@Trigger(value = "!googlefr", type = TriggerType.STARTSWITH)
	@Help("Google search (French).")
	public List<String> googlefr(TriggerEvent event) {
		return google(event.getMessage(), Lang.FRENCH, Mode.WEB);
	}

	@Trigger(value = "!image", type = TriggerType.STARTSWITH)
	@Help("Google Image search.")
	public List<String> image(TriggerEvent event) {
		return google(event.getMessage(), Lang.ENGLISH, Mode.IMAGE);
	}

	@Trigger(value = "!imagefr", type = TriggerType.STARTSWITH)
	@Help("Google Image search (French).")
	public List<String> imagefr(TriggerEvent event) {
		return google(event.getMessage(), Lang.FRENCH, Mode.IMAGE);
	}

	public List<String> google(String keywords, Lang lang, Mode mode) {
		String result = "";
		try {
			String GOOGLE_API_URL = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=off&lr=lang_"
					+ lang + "&q=";
			String GOOGLE_API_IMAGES = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&safe=off&lr=lang_"
					+ lang + "&q=";
			String url;
			if (mode == Mode.WEB) {
				url = GOOGLE_API_URL + URLEncoder.encode(keywords, "UTF-8");
			} else {
				url = GOOGLE_API_IMAGES + URLEncoder.encode(keywords, "UTF-8");
			}
			result = utilsService.getContent(url);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return parse(result, keywords, mode);
	}

	private List<String> parse(String source, String keywords, Mode mode) {
		List<String> result = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(source);

			JSONArray ja = json.getJSONObject("responseData").getJSONArray(
					"results");
			JSONObject j = ja.getJSONObject(0);
			if (mode == Mode.WEB) {
				String firstLine = StringEscapeUtils.unescapeHtml4(IRCUtils
						.bold(j.getString("titleNoFormatting")))
						+ " - "
						+ URLDecoder.decode(j.getString("url"), "UTF-8");

				result.add(firstLine);

				String content = j.getString("content");
				content = content.replaceAll("<b>", "");
				content = content.replaceAll("</b>", "");
				content = content.replaceAll("  ", "");

				content = StringEscapeUtils.unescapeHtml4(content);

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
