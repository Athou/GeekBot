package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;

@RunWith(WeldRunner.class)
public class ReverseCommandTest {

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
