package be.hehehe.geekbot.commands;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleUtil;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class MirrorCommand {
	private static String LASTURL;

	@Inject
	private BotUtilsService utilsService;

	@Trigger("!mirror")
	public String getMirrorImage() {
		String result = null;
		if (LASTURL != null) {
			result = handleImage(LASTURL);
		}
		return result;
	}

	@Trigger(value = "!mirror", type = TriggerType.STARTSWITH)
	public String getMirrorImage2(TriggerEvent event) {
		String result = handleImage(event.getMessage());
		return result;
	}

	@Trigger(type = TriggerType.EVERYTHING)
	public String storeLastURL(TriggerEvent event) {

		String url = utilsService.extractURL(event.getMessage());
		if (url != null) {
			LASTURL = url;
		}
		return null;
	}

	private String handleImage(String message) {
		String result = null;
		if (message.endsWith(".png") || message.endsWith(".jpg")
				|| message.endsWith(".gif") || message.endsWith(".bmp")) {
			try {
				result = writeImageFile(message);
			} catch (Exception e) {
				result = "Error retrieving file";
			}
		}
		return result;
	}

	private String writeImageFile(String urlString) {
		String result = null;
		OutputStreamWriter wr = null;
		InputStream is = null;
		try {
			String apiKey = BundleUtil.getImgurApiKey();
			if (StringUtils.isBlank(apiKey)) {
				return "Imgur api key not set.";
			}

			URL apiUrl = new URL("http://api.imgur.com/2/upload.json");

			String data = URLEncoder.encode("image", "UTF-8") + "="
					+ URLEncoder.encode(urlString, "UTF-8");
			data += "&" + URLEncoder.encode("key", "UTF-8") + "="
					+ URLEncoder.encode(apiKey, "UTF-8");
			data += "&" + URLEncoder.encode("type", "UTF-8") + "="
					+ URLEncoder.encode("url", "UTF-8");

			URLConnection apiCon = apiUrl.openConnection();
			apiCon.setDoOutput(true);
			apiCon.setDoInput(true);
			wr = new OutputStreamWriter(apiCon.getOutputStream());
			wr.write(data);
			wr.flush();
			is = apiCon.getInputStream();
			String json = IOUtils.toString(is);
			JSONObject root = (JSONObject) new JSONParser().parse(json);
			JSONObject node = (JSONObject) root.get("upload");
			node = (JSONObject) node.get("links");
			String url = (String) node.get("original");

			node = (JSONObject) root.get("upload");
			node = (JSONObject) node.get("image");
			Long size = (Long) node.get("size");
			size = size / 1000;
			result = url + " [Size: " + size + " kb]";
		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(wr);
			IOUtils.closeQuietly(is);
		}
		return result;
	}
}
