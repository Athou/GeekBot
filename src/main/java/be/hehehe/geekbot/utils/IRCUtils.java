package be.hehehe.geekbot.utils;

public class IRCUtils {
	private final static String BOLD = "\u0002";
	private final static String REMOVE = "\u000F";

	public static String bold(String s) {
		return BOLD + s + REMOVE;
	}
}
