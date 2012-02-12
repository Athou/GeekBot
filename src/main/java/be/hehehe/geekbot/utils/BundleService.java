package be.hehehe.geekbot.utils;

import java.util.ResourceBundle;

import javax.inject.Singleton;

@Singleton
public class BundleService {

	private final ResourceBundle bundle = ResourceBundle.getBundle("config");

	public String getBotName() {
		return getValue("botname");
	}

	public String getChannel() {
		return getValue("channel");
	}

	public String getServer() {
		return getValue("server");
	}

	public String getBitlyLogin() {
		return getValue("bitly.login");
	}

	public String getBitlyApiKey() {
		return getValue("bitly.apikey");
	}

	public String getImgurApiKey() {
		return getValue("imgur.apikey");
	}

	public String getVDMApiKey() {
		return getValue("vdm.apikey");
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

	private String getValue(String key) {
		return bundle.getString(key);
	}

}
