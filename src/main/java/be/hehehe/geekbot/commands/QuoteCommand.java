package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.QuoteDAO;
import be.hehehe.geekbot.persistence.model.Quote;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Quote engine. Store, find and get random quotes.
 * 
 */
@BotCommand
public class QuoteCommand {

	@Inject
	QuoteDAO dao;

	@Trigger("!quote")
	@Help("Prints a random quote.")
	public String getRandomQuote() {
		int rand = new Random().nextInt((int) dao.getCount()) + 1;
		return IRCUtils.bold("" + rand) + ". "
				+ dao.findByNumber(rand).getQuote();
	}

	@Trigger(value = "!quote", type = TriggerType.STARTSWITH)
	@Help("Prints the specified quote.")
	public List<String> getQuote(TriggerEvent event)
			throws NumberFormatException {
		String quoteIds = event.getMessage();
		List<String> quotes = new ArrayList<String>();
		String[] splitQuotes = quoteIds.split("[ ]");
		for (int i = 0; i < splitQuotes.length && i <= 4; i++) {
			int id = Integer.parseInt(splitQuotes[i]);
			if (id > dao.getCount()) {
				throw new NumberFormatException();
			}
			quotes.add(IRCUtils.bold("" + id) + ". "
					+ dao.findByNumber(id).getQuote());
		}
		return quotes;

	}

	@Trigger(value = "!addquote", type = TriggerType.STARTSWITH)
	@Help("Add the quote in the system.")
	public String addQuote(TriggerEvent event) {
		dao.save(new Quote(event.getMessage()));
		return "Quote added: " + (dao.getCount());
	}

	@Trigger(value = "!findquote", type = TriggerType.STARTSWITH)
	@Help("Prints a list of quotes matching the given keywords.")
	public String findQuote(TriggerEvent event) {
		List<String> keywordList = Arrays.asList(event.getMessage()
				.split("[ ]"));
		List<Quote> quotes = dao.findByKeywords(keywordList);
		String result = "";
		if (quotes.isEmpty()) {
			result = " none.";
		} else {
			for (Quote q : quotes) {
				result += " " + q.getNumber();
			}
		}
		result = IRCUtils.bold("Matching quotes:") + result;
		return result;
	}
}
