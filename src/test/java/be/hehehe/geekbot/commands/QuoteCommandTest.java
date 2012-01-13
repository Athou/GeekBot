package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.hehehe.geekbot.WeldRunner;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.bot.TriggerEventImpl;
import be.hehehe.geekbot.persistence.dao.QuoteDAO;
import be.hehehe.geekbot.persistence.model.Quote;
import be.hehehe.geekbot.utils.IRCUtils;

@RunWith(WeldRunner.class)
public class QuoteCommandTest {

	private static final String QUOTE1 = "quote1 content";
	private static final String QUOTE2 = "quote2 content";

	@Inject
	QuoteCommand quoteCommand;

	@Inject
	QuoteDAO dao;

	@Before
	public void init() {
		for (Quote quote : dao.findAll()) {
			dao.delete(quote);
		}

		dao.save(new Quote(QUOTE1));
		dao.save(new Quote(QUOTE2));

	}

	@Test
	public void quoteTest() {
		TriggerEvent event = new TriggerEventImpl("1");
		String expected = IRCUtils.bold("1") + ". " + QUOTE1;
		String actual = quoteCommand.getQuote(event).iterator().next();
		Assert.assertEquals(expected, actual);

		event = new TriggerEventImpl("2");
		expected = IRCUtils.bold("2") + ". " + QUOTE2;
		actual = quoteCommand.getQuote(event).iterator().next();
		Assert.assertEquals(expected, actual);
		
		Assert.assertEquals(2, dao.findAll().size());
		Assert.assertEquals(2, dao.getCount());
	}

	@Test
	public void findQuoteTest() {
		TriggerEvent event = new TriggerEventImpl(QUOTE1);
		String expected = IRCUtils.bold("Matching quotes:") + " 1";
		String actual = quoteCommand.findQuote(event);
		Assert.assertEquals(expected, actual);

		event = new TriggerEventImpl(QUOTE2);
		expected = IRCUtils.bold("Matching quotes:") + " 2";
		actual = quoteCommand.findQuote(event);
		Assert.assertEquals(expected, actual);

		dao.delete(dao.findByNumber(1));
		expected = IRCUtils.bold("Matching quotes:") + " 1";
		actual = quoteCommand.findQuote(event);
		Assert.assertEquals(expected, actual);

		dao.delete(dao.findByNumber(1));
		expected = IRCUtils.bold("Matching quotes:") + " none.";
		actual = quoteCommand.findQuote(event);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addQuoteTest() {
		String expected = "Quote added: 3";
		TriggerEvent event = new TriggerEventImpl("cc");
		String actual = quoteCommand.addQuote(event);

		Assert.assertEquals(expected, actual);
	}
}
