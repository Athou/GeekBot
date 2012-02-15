package be.hehehe.geekbot;

import org.jboss.weld.environment.se.Weld;

import be.hehehe.geekbot.bot.GeekBot;

public class Main {

	public static void main(String[] args) {

		new Weld().initialize().instance().select(GeekBot.class).get();
	}
}
