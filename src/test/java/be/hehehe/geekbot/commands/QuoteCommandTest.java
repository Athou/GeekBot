package be.hehehe.geekbot.commands;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.utils.IRCUtils;

public class QuoteCommandTest {

	private static final String QUOTE1 = "quote1 content";

	@BeforeClass
	public static void init() {
		TriggerEvent event = new TriggerEventImpl(QUOTE1);
		new QuoteCommand().addQuote(event);
	}

	@Test
	public void quoteTest() {
		TriggerEvent event = new TriggerEventImpl("1");
		String expected = IRCUtils.bold("1") + ". " + QUOTE1;
		String actual = new QuoteCommand().getQuote(event).iterator().next();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void findQuoteTest() {
		TriggerEvent event = new TriggerEventImpl(QUOTE1);
		String expected = IRCUtils.bold("Matching quotes:") + " 1";
		String actual = new QuoteCommand().findQuote(event);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addQuoteTest() {
		String expected = "Quote added: 2";
		TriggerEvent event = new TriggerEventImpl("cc");
		String actual = new QuoteCommand().addQuote(event);

		Assert.assertEquals(expected, actual);
	}
}
