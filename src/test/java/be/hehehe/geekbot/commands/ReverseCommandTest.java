package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;

import be.hehehe.geekbot.ArquillianTest;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

public class ReverseCommandTest extends ArquillianTest {

	@Inject
	ReverseCommand reverseCommand;

	@Test
	public void reverseTest() {
		TriggerEvent event = new TriggerEventImpl("plap nolife");
		String expected = "efilon palp";
		String actual = reverseCommand.reverse(event);
		Assert.assertEquals(expected, actual);

	}

}
