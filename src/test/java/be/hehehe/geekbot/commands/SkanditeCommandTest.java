package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

@RunWith(WeldRunner.class)
public class SkanditeCommandTest {

	@Inject
	SkanditeCommand skanditeCommand;

	@Test
	public void skanditeTest() {
		TriggerEvent event = new TriggerEventImpl("http://www.commabeat.com");
		List<String> result = skanditeCommand.handleSkandites(event);
		Assert.assertTrue(result.isEmpty());
		result = skanditeCommand.handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}
}
