package be.hehehe.geekbot;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.geekbot.bot.GeekBot;
import be.hehehe.geekbot.utils.BundleUtil;

public class Main {

	public static void main(String[] args) {

		WeldContainer weld = new Weld().initialize();
		// weld.instance().select(GeekBot.class).get();
		new GeekBot(BundleUtil.getBotName(), BundleUtil.getChannel(),
				BundleUtil.getServer());
	}
}
