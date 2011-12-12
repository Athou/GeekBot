package be.hehehe.geekbot;

import be.hehehe.geekbot.bot.GeekBot;
import be.hehehe.geekbot.utils.BundleUtil;

public class Main {

	public static void main(String[] args) {
		new GeekBot(BundleUtil.getBotName(), BundleUtil.getChannel(),
				BundleUtil.getServer());
	}
}
