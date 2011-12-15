package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

@Named
public class SkanditeCommandTest {

	@Inject
	private SkanditeCommand skanditeCommand;

	@Test
	@Ignore
	public void skanditeTest() {
		TriggerEvent event = new TriggerEventImpl("http://www.commabeat.com");
		List<String> result = skanditeCommand.handleSkandites(event);
		Assert.assertTrue(result.isEmpty());
		result = skanditeCommand.handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}

}
