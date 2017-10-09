package be.hehehe.geekbot.commands;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * Google search, web or images
 * 
 */
@BotCommand
@JBossLog
public class GoogleCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	BundleService bundleService;

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
		return google(event.getMessage(), Mode.WEB);
	}

	@Trigger(value = "!image", type = TriggerType.STARTSWITH)
	@Help("Google Image search.")
	public List<String> image(TriggerEvent event) {
		return google(event.getMessage(), Mode.IMAGE);
	}

	public List<String> google(String keywords, Mode mode) {
		String result = "";
		try {
			String key = bundleService.getGoogleKey();
			String cx = bundleService.getGoogleCseId();

			String apiUrl = "https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=" + cx;
			if (mode == Mode.IMAGE) {
				apiUrl += "&searchType=image";
			}
			String url = apiUrl + "&q=" + URLEncoder.encode(keywords, "UTF-8");
			result = utilsService.getContent(url);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return parse(result, keywords, mode);
	}

	private List<String> parse(String source, String keywords, Mode mode) {
		List<String> result = new ArrayList<>();
		try {
			JSONObject json = new JSONObject(source);

			JSONArray ja = json.getJSONArray("items");
			JSONObject j = ja.getJSONObject(0);
			if (mode == Mode.WEB) {
				String firstLine = StringEscapeUtils.unescapeHtml4(IRCUtils.bold(j.getString("title"))) + " - "
						+ URLDecoder.decode(j.getString("link"), "UTF-8");

				result.add(firstLine);

				String content = j.getString("snippet");
				content = content.replaceAll("\n", "");

				result.add(content);
			} else {
				String s = json.getJSONArray("items").getJSONObject(0).getString("link");
				result.add(IRCUtils.bold(s));
			}

		} catch (Exception e) {
			result.add("Your search - " + keywords + " - did not match any documents.");
		}

		return result;
	}

}
