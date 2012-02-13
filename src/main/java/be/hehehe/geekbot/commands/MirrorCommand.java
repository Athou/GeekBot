package be.hehehe.geekbot.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.ServletMethod;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.LOG;

/**
 * Mirrors images on imgur.com, useful for image hosts blocked at work. When the
 * trigger is invoked without arguments, the last url on the channel will be
 * mirrored.
 * 
 */
@BotCommand
public class MirrorCommand {

	private static final String KEY_TEMPFILE = "tempfile";
	private static final String KEY_VIDEOURL = "videourl";

	@Inject
	State state;

	@Inject
	BundleService bundleService;

	@Trigger("!mirror")
	@Help("Mirrors the last link pasted on the chan.")
	public String getMirrorImage(TriggerEvent event) {
		String result = null;
		String lastUrl = state.get(String.class);
		if (lastUrl != null) {
			result = handleURL(lastUrl, event);
		}
		return result;
	}

	@Trigger(value = "!mirror", type = TriggerType.STARTSWITH)
	@Help("Mirrors the given url.")
	public String getMirrorImage2(TriggerEvent event) {
		String result = handleURL(event.getURL(), event);
		return result;
	}

	@Trigger(type = TriggerType.EVERYTHING)
	public void storeLastURL(TriggerEvent event) {
		if (event.hasURL()) {
			state.put(event.getURL());
		}
		return;
	}

	private String handleURL(String message, TriggerEvent event) {
		String result = null;
		if (message.endsWith(".png") || message.endsWith(".jpg")
				|| message.endsWith(".gif") || message.endsWith(".bmp")) {
			try {
				result = mirrorImage(message);
			} catch (Exception e) {
				result = "Error retrieving file";
			}
		} else {
			result = mirrorVideo(message, event);
		}
		return result;
	}

	private String mirrorImage(String urlString) {
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

	private String mirrorVideo(String message, TriggerEvent event) {
		String result = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			event.write("Mirroring " + message);
			File tempFile = state.get(KEY_TEMPFILE, File.class);
			if (tempFile == null) {
				tempFile = File.createTempFile("video_", ".mp4");
				tempFile.deleteOnExit();
				state.put(KEY_TEMPFILE, tempFile);
			}
			state.put(KEY_VIDEOURL, message);
			LOG.debug("Mirroring " + message + " at "
					+ tempFile.getAbsolutePath());
			String[] movgrab = new String[] { "movgrab", "-o",
					tempFile.getAbsolutePath(), "-f", "mp4,flv", message };
			Process process = Runtime.getRuntime().exec(movgrab);
			LOG.debug(IOUtils.toString(process.getErrorStream()));

			result = "Mirrored here : " + bundleService.getWebServerRootPath()
					+ "/videomirror?t="
					+ Calendar.getInstance().getTimeInMillis();
		} catch (Exception e) {
			result = e.getMessage();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
		return result;
	}

	@ServletMethod("/videomirror")
	public void renderPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String url = state.get(KEY_VIDEOURL, String.class);
		if (url != null) {
			request.setAttribute("url", url);
			request.setAttribute(
					"video",
					URLEncoder.encode(bundleService.getWebServerRootPath()
							+ "/videostream.mp4", "UTF-8"));
			request.getRequestDispatcher("/videomirror.jsp").forward(request,
					response);
		}
	}

	@ServletMethod("/videostream.mp4")
	public void streamVideo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		File file = state.get(KEY_TEMPFILE, File.class);
		InputStream is = null;
		if (file != null) {
			try {
				is = new FileInputStream(file);
				response.setContentType("video/mp4");
				OutputStream os = response.getOutputStream();
				IOUtils.copy(is, os);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
