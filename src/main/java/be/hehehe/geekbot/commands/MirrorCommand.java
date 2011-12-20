package be.hehehe.geekbot.commands;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.LOG;

/**
 * Mirrors images on imgur.com, useful for image hosts blocked at work. when the
 * trigger is invoked without arguments, the last url on the channel will be
 * mirrored.
 * 
 */
@BotCommand
public class MirrorCommand {
	private static String LASTURL;

	@Inject
	BotUtilsService utilsService;

	@Inject
	BundleService bundleService;

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
				result = mirror(message);
			} catch (Exception e) {
				result = "Error retrieving file";
			}
		}
		return result;
	}

	private String mirror(String urlString) {
		String result = null;
		OutputStreamWriter wr = null;
		InputStream is = null;
		try {
			String apiKey = bundleService.getImgurApiKey();
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
			JSONObject root = new JSONObject(json);
			JSONObject node = root.getJSONObject("upload").getJSONObject(
					"links");

			String url = node.getString("original");

			node = root.getJSONObject("upload");
			node = node.getJSONObject("image");
			Long size = node.getLong("size");
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
