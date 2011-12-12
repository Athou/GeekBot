package be.hehehe.geekbot.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class BotUtils {
	public static String getContent(String urlString) {
		String result = null;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
			is = con.getInputStream();
			result = IOUtils.toString(is);
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
}
