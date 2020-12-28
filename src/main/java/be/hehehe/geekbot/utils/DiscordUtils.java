package be.hehehe.geekbot.utils;

public class DiscordUtils {

	public static String bold(String s) {
		return "**" + s + "**";
	}

	public static String linkWithoutPreview(String linkUrl) {
		return "<" + linkUrl + ">";
	}
}
