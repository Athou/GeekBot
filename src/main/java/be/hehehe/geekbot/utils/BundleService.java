package be.hehehe.geekbot.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.IOUtils;

import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@JBossLog
public class BundleService {

	private Properties props;

	@PostConstruct
	public void init() {
		props = new Properties();
		InputStream is = null;
		try {
			log.info("loading config file");
			props.load(new FileInputStream("config.properties"));
		} catch (Exception e) {
			log.fatal("Could not load config file", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public String getAdminPassword() {
		return getValue("admin.password");
	}

	public String getBotName() {
		return getValue("botname");
	}

	public List<String> getChannels() {
		return Arrays.asList(getValue("channels").split(Pattern.quote(",")));
	}

	public String getDiscordToken() {
		return getValue("discord.token");
	}

	public String getBitlyLogin() {
		return getValue("bitly.login");
	}

	public String getBitlyApiKey() {
		return getValue("bitly.apikey");
	}

	public String getImgurClientId() {
		return getValue("imgur.clientId");
	}

	public String getVDMApiKey() {
		return getValue("vdm.apikey");
	}

	public String getMemeGeneratorLogin() {
		return getValue("meme.login");
	}

	public String getMemeGeneratorPassword() {
		return getValue("meme.password");
	}

	public String getWebServerRootPath() {
		String host = getValue("webserver.hostname");
		if (host.endsWith("/")) {
			host = host.substring(0, host.length() - 1);
		}
		return host;
	}

	public int getWebServerPort() {
		return Integer.parseInt(getValue("webserver.port"));
	}

	public String getTwitterConsumerKey() {
		return getValue("twitter.consumerKey");
	}

	public String getTwitterConsumerSecret() {
		return getValue("twitter.consumerSecret");
	}

	public String getTwitterToken() {
		return getValue("twitter.token");
	}

	public String getTwitterTokenSecret() {
		return getValue("twitter.tokenSecret");
	}

	public String getGoogleKey() {
		return getValue("google.key");
	}

	public String getGoogleCseId() {
		return getValue("google.cseId");
	}

	public boolean isTest() {
		return "true".equals(getValue("test"));
	}

	public String getDiscordBotName() {
		return getValue("discord.botname");
	}

	public String getValue(String key) {
		return props.getProperty(key, null);
	}

}
