package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import be.hehehe.geekbot.persistence.model.Quote;

import com.google.common.collect.Lists;

public class QuoteDAO extends GenericDAO<Quote> {

	@Override
	public void save(Quote object) {
		object.setNumber((int) getCount() + 1);
		super.save(object);
	}

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
	public void delete(Quote object) {
		super.delete(object);
		int i = 1;
		List<Quote> quotes = findAll();
		for (Quote quote : quotes) {
			quote.setNumber(i);
			i++;
		}
		update(quotes.toArray(new Quote[0]));
	}

	public Quote findByNumber(int number) {
		Quote quote = new Quote();
		quote.setNumber(number);
		return findByExample(quote).iterator().next();
	}
}
