package be.hehehe.geekbot.commands;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

public class SkanditeCommandTest {

	@Test
	public void skanditeTest() {
		SkanditeCommand skanditeCommand = new SkanditeCommand();
		TriggerEvent event = new TriggerEventImpl("http://www.commabeat.com");
		List<String> result = skanditeCommand.handleSkandites(event);
		Assert.assertTrue(result.isEmpty());
		result = skanditeCommand.handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}

}
