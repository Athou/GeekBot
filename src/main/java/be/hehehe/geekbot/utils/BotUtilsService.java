package be.hehehe.geekbot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;

import com.google.common.collect.Maps;

@ApplicationScoped
public class BotUtilsService {

	@Inject
	BundleService bundleService;

	@Inject
	Logger log;

	/**
	 * Get the content of the specified URL
	 * 
	 * @param urlString
	 * @return the content of the url, null if unable to fetch.
	 */
	public String getContent(String urlString) {
		return getContent(urlString, null);
	}

	/**
	 * Get the content of the specified URL only if the MIME type of the target
	 * starts with mimeTypePrefix or if mimeTypePrefix is null.
	 * 
	 * @param urlString
	 * @param mimeTypePrefix
	 * @return the content of the url, null if unable to fetch.
	 */
	public String getContent(String urlString, String mimeTypePrefix) {
		String result = null;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			String contentType = con.getContentType();
			if (mimeTypePrefix == null
					|| contentType.startsWith(mimeTypePrefix)) {
				is = con.getInputStream();
				result = IOUtils.toString(is);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	/**
	 * Shortens the specified url if possible, returning the original url if
	 * not.
	 * 
	 * @param url
	 * @return the shorten url, the original url if unable to do so.
	 */
	public String bitly(String url) {
		String result = url;
		InputStream is = null;
		String login = bundleService.getBitlyLogin();
		String apiKey = bundleService.getBitlyApiKey();
		if (StringUtils.isNotBlank(login) && StringUtils.isNotBlank(apiKey)) {
			try {
				URL urlObject = new URL(
						"http://api.bit.ly/shorten?version=2.0.1&longUrl="
								+ url + "&login=" + login + "&apiKey=" + apiKey);
				result = IOUtils.toString(urlObject.openStream());
				JSONObject json = new JSONObject(result);
				result = json.getJSONObject("results").getJSONObject(url)
						.getString("shortUrl");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		return result;
	}

	/**
	 * Mirrors image on imgur
	 */
	public String mirrorImage(String urlString) {
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
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(wr);
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	/**
	 * Parses an url and return a map of request parameters
	 * 
	 * @param url
	 * @return the map of request parameters
	 */
	public Map<String, String> getRequestParametersFromURL(String url) {
		Map<String, String> map = Maps.newHashMap();
		int index;
		if ((index = url.indexOf("?")) != -1) {
			for (String keyvalue : url.substring(index + 1).split("&")) {
				String[] split = keyvalue.split("=");
				if (split.length == 2) {
					map.put(split[0], split[1]);
				}
			}
		}
		return map;
	}

	/**
	 * Returns the first url found in the given string, or null if no url is
	 * found.
	 * 
	 * @param message
	 * @return an url string or null if none found.
	 */
	public String extractURL(String message) {
		String url = null;
		if (message == null) {
			return null;
		}
		for (String s : message.split(" ")) {
			if (s.contains("http://") || s.contains("https://")
					|| s.contains("www.")) {
				if (s.startsWith("www.")) {
					s = "http://" + s;
				}
				if (s.endsWith("/")) {
					s = s.substring(0, s.length() - 1);
				}

				if (s.contains("youtube.com")) {
					String videoParam = getRequestParametersFromURL(s).get("v");
					if (videoParam != null) {
						s = "http://www.youtube.com/watch?v=" + videoParam;
					}
				} else if (s.contains("youtu.be")) {
					String videoParam = extractIDFromYoutuDotBeURL(s);
					s = "http://www.youtube.com/watch?v=" + videoParam;
				} else {
					Map<String, String> map = getRequestParametersFromURL(s);
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

	public String extractIDFromYoutuDotBeURL(String url) {
		String videoParam = url.substring(url.lastIndexOf("/") + 1);
		int indexOfSlash = videoParam.indexOf("?");
		if (indexOfSlash > -1) {
			videoParam = videoParam.substring(0, indexOfSlash);
		}
		return videoParam;
	}

	public String getTimeDifference(Date pastDate) {
		long millis = System.currentTimeMillis() - pastDate.getTime();
		long diffMins = (millis / (60 * 1000)) % 60;
		long diffHours = (millis / (60 * 60 * 1000)) % 24;
		long diffDays = millis / (24 * 60 * 60 * 1000);
		return String.format("%d days %d hours %d minutes", diffDays,
				diffHours, diffMins);
	}

	public HashAndByteCount calculateHashAndByteCount(String urlString) {
		HashAndByteCount hashAndByteCount = new HashAndByteCount();

		String content = getContent(urlString, "image/");
		if (content != null) {
			byte[] bytes = content.getBytes();

			hashAndByteCount.setHash(DigestUtils.md5Hex(bytes));
			hashAndByteCount.setByteCount(Long.valueOf(bytes.length));
		}
		return hashAndByteCount;
	}

	public Document parseXML(String xml) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(xml));
		return doc;
	}
	
	public String stripAccents(String source) {
		source = Normalizer.normalize(source, Normalizer.Form.NFD);
		source = source.replaceAll("[\u0300-\u036F]", "");
		return source;
	}
}
