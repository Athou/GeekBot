package be.hehehe.geekbot;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.geekbot.bot.GeekBot;

public class Main {

	public static void main(String[] args) {

		WeldContainer weld = new Weld().initialize();
		weld.instance().select(GeekBot.class).get();
	}
}
