package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import be.hehehe.geekbot.persistence.model.Quote;

@Singleton
public class QuoteDAO extends GenericDAO<Quote> {

	@Override
	public void save(Quote object) {
		object.setNumber((int) getCount() + 1);
		super.save(object);
	}

	@SuppressWarnings("unchecked")
	public List<Quote> findByKeywords(List<String> keywords) {
		Criteria crit = createCriteria();
		for (String keyword : keywords) {
			crit.add(Restrictions.ilike("quote", "%" + keyword + "%"));
		}
		return crit.list();
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
		return (Quote) createCriteria().add(Restrictions.eq("number", number))
				.uniqueResult();
	}
}
