package be.hehehe.geekbot.utils;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import be.hehehe.geekbot.bot.GeekBot;

@Singleton
@Startup
public class StartupBean {

	@Inject
	GeekBot bot;

}
