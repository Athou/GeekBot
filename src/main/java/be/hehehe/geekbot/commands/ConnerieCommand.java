package be.hehehe.geekbot.commands;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.dao.SkanditeDAO;
import be.hehehe.geekbot.persistence.lucene.ConnerieIndex;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.persistence.model.Skandite;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.BundleUtil;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;
import be.hehehe.geekbot.utils.URLBuilder;

@BotCommand
public class ConnerieCommand {

	private static final List<String> lastSentences = new ArrayList<String>();
	private static final int MAXSENTENCES = 3;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> storeEveryLines(String message, String author,
			boolean nickInMessage) {
		String url = extractURL(message);
		if (url == null) {
			pushSentence(message);
			if (!nickInMessage && message.length() > 9
					&& !message.contains("<") && !message.contains(">")
					&& !message.startsWith("!")) {
				ConnerieDAO dao = new ConnerieDAO();
				Connerie connerie = new Connerie(message);
				dao.save(connerie);
			}
		}
		List<String> result = new ArrayList<String>();
		if (url != null) {
			MirrorCommand.LASTURL = url;
			SkanditeDAO dao = new SkanditeDAO();

			Skandite skandite = dao.findByURL(url);
			if (skandite != null) {
				String line = IRCUtils.bold("Skandite! ") + skandite.getUrl()
						+ " linked "
						+ getTimeDifference(skandite.getPostedDate())
						+ " ago by " + skandite.getAuthor() + " ("
						+ skandite.getCount() + "x).";
				result.add(line);
				skandite.setCount(skandite.getCount() + 1);
				dao.save(skandite);
			} else {
				HashAndByteCount hashAndByteCount = calculateHashAndByteCount(url);
				if (hashAndByteCount != null) {
					skandite = dao.findByHashAndByteCount(
							hashAndByteCount.hash, hashAndByteCount.byteCount);
				}
				if (skandite != null) {
					String line = IRCUtils.bold("Skandite! ")
							+ skandite.getUrl() + " linked "
							+ getTimeDifference(skandite.getPostedDate())
							+ " ago by " + skandite.getAuthor() + " ("
							+ skandite.getCount() + "x).";
					result.add(line);
					skandite.setCount(skandite.getCount() + 1);
					dao.save(skandite);
				} else {
					skandite = new Skandite();
					skandite.setUrl(url);
					skandite.setPostedDate(new Date());
					skandite.setAuthor(author);
					skandite.setCount(1l);
					if (hashAndByteCount != null) {
						skandite.setHash(hashAndByteCount.hash);
						skandite.setByteCount(hashAndByteCount.byteCount);
					}
					dao.save(skandite);
				}
			}
			if (url.contains("youtube.com") || url.contains("youtu.be")) {
				String videoParam = null;
				if (url.contains("youtube.com")) {
					videoParam = getRequestParametersFromURL(url).get("v");
				} else {
					videoParam = getIDFromURL(url);
				}
				String data = "http://gdata.youtube.com/feeds/api/videos/"
						+ videoParam;
				try {
					String content = BotUtils.getContent(data);
					SAXBuilder builder = new SAXBuilder();
					Document doc = builder.build(new StringReader(content));
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
				String videoParam = getIDFromURL(url);
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

	private void pushSentence(String message) {
		lastSentences.add(0, message);
		if (lastSentences.size() > MAXSENTENCES) {
			lastSentences.remove(MAXSENTENCES);
		}

	}

	public class HashAndByteCount {
		public String hash;
		public Long byteCount;
	}

	public HashAndByteCount calculateHashAndByteCount(String urlString) {
		HashAndByteCount hashAndByteCount = null;
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(60000);
			con.setReadTimeout(60000);
			String contentType = con.getContentType();
			if (StringUtils.isNotBlank(contentType)
					&& contentType.startsWith("image/")) {
				is = con.getInputStream();
				IOUtils.copy(is, baos);
				byte[] bytes = baos.toByteArray();
				hashAndByteCount = new HashAndByteCount();
				hashAndByteCount.hash = DigestUtils.md5Hex(bytes);
				hashAndByteCount.byteCount = new Long(bytes.length);
			}
		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(baos);
		}
		return hashAndByteCount;
	}

	@RandomAction(3)
	@Trigger(type = TriggerType.BOTNAME)
	public String getRandomLine(String message) {
		String botName = BundleUtil.getBotName();
		message = message.replace(botName, "");
		message = message.replace("?", "");
		List<String> list = ConnerieIndex.findRelated(message, lastSentences);
		Random random = new Random();
		int irand = random.nextInt(list.size());
		String msg = list.get(irand);
		return msg;
	}

	private String getTimeDifference(Date pastDate) {
		long millis = System.currentTimeMillis() - pastDate.getTime();
		long diffMins = (millis / (60 * 1000)) % 60;
		long diffHours = (millis / (60 * 60 * 1000)) % 24;
		long diffDays = millis / (24 * 60 * 60 * 1000);
		return String.format("%d days %d hours %d minutes", diffDays,
				diffHours, diffMins);
	}

	private String extractURL(String message) {
		String url = null;
		for (String s : message.split("[ ]")) {
			if (s.contains("http://") || s.contains("https://")
					|| s.contains("www.")) {
				if (s.endsWith("/")) {
					s = s.substring(0, s.length() - 1);
				}

				if (s.contains("youtube.com")) {
					String videoParam = getRequestParametersFromURL(s).get("v");
					if (videoParam != null) {
						s = "http://www.youtube.com/watch?v=" + videoParam;
					}
				} else if (s.contains("youtu.be")) {
					String videoParam = getIDFromURL(s);
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

	private Map<String, String> getRequestParametersFromURL(String url) {
		Map<String, String> map = new HashMap<String, String>();
		int index;
		if ((index = url.indexOf("?")) != -1) {
			for (String keyvalue : url.substring(index + 1).split("&")) {
				String[] split = keyvalue.split("=");
				map.put(split[0], split[1]);
			}
		}
		return map;
	}

	private String getIDFromURL(String url) {
		String videoParam = url.substring(url.lastIndexOf("/") + 1);
		int indexOfSlash = videoParam.indexOf("?");
		if (indexOfSlash > -1) {
			videoParam = videoParam.substring(0, indexOfSlash);
		}
		return videoParam;
	}

}
