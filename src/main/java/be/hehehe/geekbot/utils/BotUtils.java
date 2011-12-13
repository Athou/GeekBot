package be.hehehe.geekbot.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.google.common.collect.Maps;

public class BotUtils {

	public static String getContent(String urlString) {
		return getContent(urlString, null);
	}

	public static String getContent(String urlString, String mimeTypePrefix) {
		String result = null;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
			con.setConnectTimeout(60000);
			con.setReadTimeout(60000);
			String contentType = con.getContentType();
			if (mimeTypePrefix == null
					|| contentType.startsWith(mimeTypePrefix)) {
				is = con.getInputStream();
				result = IOUtils.toString(is);
			}
		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	public static String bitly(String urlz) {
		String result = "";
		InputStream is = null;
		String login = BundleUtil.getBitlyLogin();
		String apiKey = BundleUtil.getBitlyApiKey();
		if (StringUtils.isBlank(login) || StringUtils.isBlank(apiKey)) {
			return urlz;
		}
		try {
			URL url = new URL(
					"http://api.bit.ly/shorten?version=2.0.1&longUrl=" + urlz
							+ "&login=" + login + "&apiKey=" + apiKey);
			result = IOUtils.toString(url.openStream());
			JSONObject json = new JSONObject(result);
			result = json.getJSONObject("results").getJSONObject(urlz)
					.getString("shortUrl");
		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	public static Map<String, String> getRequestParametersFromURL(String url) {
		Map<String, String> map = Maps.newHashMap();
		int index;
		if ((index = url.indexOf("?")) != -1) {
			for (String keyvalue : url.substring(index + 1).split("&")) {
				String[] split = keyvalue.split("=");
				map.put(split[0], split[1]);
			}
		}
		return map;
	}

	public static String extractURL(String message) {
		String url = null;
		for (String s : message.split("[ ]")) {
			if (s.contains("http://") || s.contains("https://")
					|| s.contains("www.")) {
				if (s.endsWith("/")) {
					s = s.substring(0, s.length() - 1);
				}

				if (s.contains("youtube.com")) {
					String videoParam = BotUtils.getRequestParametersFromURL(s)
							.get("v");
					if (videoParam != null) {
						s = "http://www.youtube.com/watch?v=" + videoParam;
					}
				} else if (s.contains("youtu.be")) {
					String videoParam = extractIDFromYoutuDotBeURL(s);
					s = "http://www.youtube.com/watch?v=" + videoParam;
				} else {
					Map<String, String> map = BotUtils
							.getRequestParametersFromURL(s);
					URLBuilder urlBuilder = new URLBuilder(s.split("[?]")[0]);
					for (Map.Entry<String, String> e : map.entrySet()) {
						urlBuilder.addParam(e.getKey(), e.getValue());
					}
					s = urlBuilder.build();
				}
				url = s;
				break;
			}
		}
		return url;
	}

	public static String extractIDFromYoutuDotBeURL(String url) {
		String videoParam = url.substring(url.lastIndexOf("/") + 1);
		int indexOfSlash = videoParam.indexOf("?");
		if (indexOfSlash > -1) {
			videoParam = videoParam.substring(0, indexOfSlash);
		}
		return videoParam;
	}

	public static String getTimeDifference(Date pastDate) {
		long millis = System.currentTimeMillis() - pastDate.getTime();
		long diffMins = (millis / (60 * 1000)) % 60;
		long diffHours = (millis / (60 * 60 * 1000)) % 24;
		long diffDays = millis / (24 * 60 * 60 * 1000);
		return String.format("%d days %d hours %d minutes", diffDays,
				diffHours, diffMins);
	}

	public static HashAndByteCount calculateHashAndByteCount(String urlString) {
		HashAndByteCount hashAndByteCount = new HashAndByteCount();

		String content = BotUtils.getContent(urlString, "image/");
		if (content != null) {
			byte[] bytes = content.getBytes();

			hashAndByteCount.setHash(DigestUtils.md5Hex(bytes));
			hashAndByteCount.setByteCount(new Long(bytes.length));
		}
		return hashAndByteCount;
	}
}
