package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import be.hehehe.geekbot.persistence.model.Connerie;

@Singleton
public class ConnerieDAO extends GenericDAO<Connerie> {

	public Connerie getRandomMatching(String... keywords) {
		Connerie con = getConnerie(true, keywords);
		if (con == null) {
			con = getConnerie(false, keywords);
		}
		return con;
	}

	public int getCountMatching(String... keywords) {
		List<Connerie> list = getConneries(true, keywords);
		if (list == null) {
			list = getConneries(false, keywords);
		}

		return list.size();
	}

	private Connerie getConnerie(boolean spaces, String... keywords) {
		List<Connerie> list = getConneries(spaces, keywords);
		Connerie con = null;
		if (!list.isEmpty()) {
			double rand = Math.random();
			double index = Math.floor(rand * list.size());
			con = list.get((int) index);
		}
		return con;
	}

	@SuppressWarnings("unchecked")
	private List<Connerie> getConneries(boolean spaces, String... keywords) {
		Criteria crit = createCriteria();
		for (String keyword : keywords) {
			if (spaces) {
				crit.add(Restrictions.ilike("value", "% " + keyword + " %"));
			} else {
				crit.add(Restrictions.ilike("value", "%" + keyword + "%"));
			}
		}
		return crit.list();
	}
}
