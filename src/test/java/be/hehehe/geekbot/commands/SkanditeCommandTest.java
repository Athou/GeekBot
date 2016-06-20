package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.HashAndByteCount;

public class SkanditeCommandTest extends ArquillianTest {

	@Inject
	SkanditeCommand skanditeCommand;

	@Before
	public void init() {
		skanditeCommand.utilsService = new BotUtilsService() {
			@Override
			public HashAndByteCount calculateHashAndByteCount(String urlString) {
				HashAndByteCount hashAndByteCount = new HashAndByteCount();
				hashAndByteCount.setByteCount((long) urlString.length());
				hashAndByteCount.setHash(urlString);
				return hashAndByteCount;
			}
		};
	}

	@Test
	public void skanditeTest() {
		TriggerEvent event = new TriggerEventImpl("http://www.google.com", "http://www.google.com", "author1");
		List<String> result = skanditeCommand.handleSkandites(event);
		Assert.assertTrue(result.isEmpty());

		event = new TriggerEventImpl("http://www.google.com", "http://www.google.com", "author2");
		result = skanditeCommand.handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}
}
