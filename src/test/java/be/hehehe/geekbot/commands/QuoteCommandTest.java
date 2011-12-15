package be.hehehe.geekbot.commands;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import be.hehehe.geekbot.WeldTest;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.utils.IRCUtils;

public class QuoteCommandTest extends WeldTest {

	private static final String QUOTE1 = "quote1 content";

	@BeforeClass
	public static void init() {
		TriggerEvent event = new TriggerEventImpl(QUOTE1);
		lookup(QuoteCommand.class).addQuote(event);
	}

	@Test
	public void quoteTest() {
		TriggerEvent event = new TriggerEventImpl("1");
		String expected = IRCUtils.bold("1") + ". " + QUOTE1;
		String actual = lookup(QuoteCommand.class).getQuote(event).iterator()
				.next();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void findQuoteTest() {
		TriggerEvent event = new TriggerEventImpl(QUOTE1);
		String expected = IRCUtils.bold("Matching quotes:") + " 1";
		String actual = lookup(QuoteCommand.class).findQuote(event);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addQuoteTest() {
		String expected = "Quote added: 2";
		TriggerEvent event = new TriggerEventImpl("cc");
		String actual = lookup(QuoteCommand.class).addQuote(event);

		Assert.assertEquals(expected, actual);
	}
}
