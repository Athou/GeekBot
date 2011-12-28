package be.hehehe.geekbot.persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jibble.jmegahal.JMegaHal;

import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.LOG;

@Named
@Singleton
public class ConnerieGenerator {

	private JMegaHal hal;

	@Inject
	ConnerieDAO dao;

	@PostConstruct
	public void init() {
		build();
	}

	public String buildSentence() {
		return hal.getSentence();
	}

	private void build() {
		ExecutorService executor = Executors.newCachedThreadPool();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				JMegaHal halLocal = new JMegaHal();
				int i = 1;
				for (Connerie connerie : dao.findAll()) {
					halLocal.add(connerie.getValue());
					LOG.debug("" + i);
					i++;
				}
				hal = halLocal;
			}
		};
		executor.submit(runnable);
	}
}
