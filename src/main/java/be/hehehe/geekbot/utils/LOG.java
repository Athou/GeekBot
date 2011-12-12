package be.hehehe.geekbot.utils;

import org.apache.log4j.Logger;

public class LOG {

	private static Logger log = Logger.getLogger(LOG.class);

	public static void debug(String message) {
		log.debug(message);
	}

	public static void info(String message) {
		log.info(message);
	}

	public static void warn(String message) {
		log.warn(message);
	}

	public static void error(String message) {
		log.error(message);
	}

	public static void handle(Throwable t) {
		log.error(t.getMessage(), t);
	}
}
