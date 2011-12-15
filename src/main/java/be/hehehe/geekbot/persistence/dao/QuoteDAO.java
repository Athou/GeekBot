package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import be.hehehe.geekbot.persistence.model.Quote;

import com.google.common.collect.Lists;

public class QuoteDAO extends GenericDAO<Quote> {

	public List<Quote> findByKeywords(List<String> keywords) {
		List<Quote> quotes = Lists.newArrayList();
		int i = 1;
		for (Quote quote : findAll()) {
			boolean match = true;
			for (String keyword : keywords) {
				if (!quote.getQuote().contains(keyword)) {
					match = false;
					break;
				}
			}
			if (match) {
				quote.setId((long) i);
				quotes.add(quote);
			}
			i++;
		}
		return quotes;
	}

	@Override
	public Quote findById(long id) {
		return findAll().get((int) id - 1);
	}
}
