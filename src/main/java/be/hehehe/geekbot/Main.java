package be.hehehe.geekbot;

import javax.enterprise.inject.Instance;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.geekbot.bot.GeekBot;

public class Main {

	static WeldContainer container;

	public static void main(String[] args) {
		container = new Weld().initialize();
		container.instance().select(GeekBot.class).get();

	}

	public static WeldContainer getContainer() {
		return container;
	}

	public static Instance<Object> getInstance() {
		return container.instance();
	}
}
