package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.Quote;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Stateless
public class QuoteDAO extends GenericDAO<Quote> {

	@Override
	public void save(Quote object) {
		object.setNumber((int) getCount() + 1);
		super.save(object);
	}

	public List<Quote> findByKeywords(List<String> keywords) {
		CriteriaQuery<Quote> query = builder.createQuery(Quote.class);
		Root<Quote> root = query.from(Quote.class);
		Path<String> value = root.get("quote");

		List<Predicate> predicates = Lists.newArrayList();
		for (String keyword : keywords) {
			Predicate p = builder.like(builder.lower(value), "%" + keyword.toLowerCase() + "%");
			predicates.add(p);
		}
		query.where(predicates.toArray(new Predicate[] {}));
		return em.createQuery(query).getResultList();

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
		return Iterables.getOnlyElement(findByField("number", number), null);
	}

	@Override
	protected Class<Quote> getType() {
		return Quote.class;
	}
}
