package be.hehehe.geekbot.utils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import be.hehehe.geekbot.bot.GeekBot;
import lombok.extern.jbosslog.JBossLog;

@Singleton
@Startup
@JBossLog
public class StartupBean {

	@Inject
	GeekBot bot;

	public StartupBean() {
		log.info("Bot initializing");
	}

	@PostConstruct
	private void init() {
		log.info("Bot initialized");
	}

}
