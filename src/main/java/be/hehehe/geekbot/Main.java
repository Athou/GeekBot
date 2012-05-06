package be.hehehe.geekbot;

import javax.enterprise.inject.Instance;

import org.jboss.weld.environment.se.Weld;

import be.hehehe.geekbot.bot.GeekBot;

public class Main {

	private static Instance<Object> instance;

	public static void main(String[] args) {
		instance = new Weld().initialize().instance();
		instance.select(GeekBot.class).get();
	}

	public static Instance<Object> getInstance() {
		return instance;
	}
}
