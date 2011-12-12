package be.hehehe.geekbot.utils;

import java.util.ResourceBundle;

public class BundleUtil {

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle("config");

	public static String getBotName() {
		return getValue("botname");
	}

	public static String getChannel() {
		return getValue("channel");
	}

	public static String getServer() {
		return getValue("server");
	}

	public static String getLuceneDirectory() {
		return getValue("lucene");
	}

	public static String getCommandsPackage() {
		return getValue("commands.package");
	}

	public static String getBitlyLogin() {
		return getValue("bitly.login");
	}

	public static String getBitlyApiKey() {
		return getValue("bitly.apikey");
	}

	public static String getImgurApiKey() {
		return getValue("imgur.apikey");
	}

	public static String getVDMApiKey() {
		return getValue("vdm.apikey");
	}

	private static String getValue(String key) {
		return bundle.getString(key);
	}

}
