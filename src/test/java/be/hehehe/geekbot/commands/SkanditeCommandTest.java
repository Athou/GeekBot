package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import be.hehehe.geekbot.WeldTest;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

@Named
public class SkanditeCommandTest extends WeldTest {

	@Test
	public void skanditeTest() {
		TriggerEvent event = new TriggerEventImpl("http://www.commabeat.com");
		List<String> result = lookup(SkanditeCommand.class).handleSkandites(
				event);
		Assert.assertTrue(result.isEmpty());
		result = lookup(SkanditeCommand.class).handleSkandites(event);
		Assert.assertFalse(result.isEmpty());
	}

}
