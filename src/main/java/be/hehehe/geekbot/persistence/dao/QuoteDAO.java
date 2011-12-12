package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import be.hehehe.geekbot.persistence.model.Quote;

public class QuoteDAO extends GenericDAO<Quote> {

	@SuppressWarnings("unchecked")
	public List<Quote> findByKeywords(List<String> keywords) {
		Criteria crit = createCriteria();
		for (String keyword : keywords) {
			crit.add(Restrictions.ilike("quote", "%" + keyword + "%"));
		}
		return crit.list();
	}
}
