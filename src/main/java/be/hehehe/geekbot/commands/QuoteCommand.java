package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.persistence.dao.QuoteDAO;
import be.hehehe.geekbot.persistence.model.Quote;
import be.hehehe.geekbot.utils.IRCUtils;

@BotCommand
public class QuoteCommand {

	@Trigger("!quote")
	public String getRandomQuote() {
		QuoteDAO dao = new QuoteDAO();
		double rand = Math.floor(Math.random() * dao.getCount()) + 1;
		int irand = (int) rand;
		return IRCUtils.bold("" + irand) + ". "
				+ dao.findById(irand).getQuote();
	}

	@Trigger(value = "!quote", type = TriggerType.STARTSWITH)
	public List<String> getQuote(String quoteIds) throws NumberFormatException {
		List<String> quotes = new ArrayList<String>();
		QuoteDAO dao = new QuoteDAO();
		String[] splitQuotes = quoteIds.split("[ ]");
		for (int i = 0; i < splitQuotes.length && i <= 4; i++) {
			int id = Integer.parseInt(splitQuotes[i]);
			if (id > dao.getCount()) {
				throw new NumberFormatException();
			}
			quotes.add(IRCUtils.bold("" + id) + ". "
					+ dao.findById(id).getQuote());
		}
		return quotes;

	}

	@Trigger(value = "!addquote", type = TriggerType.STARTSWITH)
	public String addQuote(String quote) {
		Quote quoteObj = new Quote();
		quoteObj.setQuote(quote);
		QuoteDAO dao = new QuoteDAO();
		dao.save(quoteObj);
		return "Quote added: " + (dao.getCount());
	}

	@Trigger(value = "!findquote", type = TriggerType.STARTSWITH)
	public String findQuote(String keywords) {
		List<String> keywordList = Arrays.asList(keywords.split("[ ]"));
		QuoteDAO dao = new QuoteDAO();
		List<Quote> quotes = dao.findByKeywords(keywordList);
		String result = "";
		if (quotes.isEmpty()) {
			result = " none.";
		} else {
			for (Quote q : quotes) {
				result += " " + q.getId();
			}
		}
		result = IRCUtils.bold("Matching quotes:") + result;
		return result;
	}
}
