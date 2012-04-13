package be.hehehe.geekbot.commands;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.HashAndByteCount;

@RunWith(WeldRunner.class)
public class SkanditeCommandTest {

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

			@Override
			public String getTimeDifference(Date pastDate) {
				return "dummy";
			}
		};
	}

	@Test
	public void skanditeTest() {
		TriggerEvent event = new TriggerEventImpl("http://www.google.com",
				"http://www.google.com", "author1");
		List<String> result = skanditeCommand.handleSkandites(event);
		Assert.assertTrue(result.isEmpty());

		event = new TriggerEventImpl("http://www.google.com",
				"http://www.google.com", "author2");
		result = skanditeCommand.handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}
}
