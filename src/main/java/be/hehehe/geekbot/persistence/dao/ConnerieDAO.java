package be.hehehe.geekbot.persistence.dao;

import java.util.List;
import java.util.Random;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.persistence.model.Connerie_;

import com.google.common.collect.Lists;

@Singleton
public class ConnerieDAO extends GenericDAO<Connerie> {

	public Connerie getRandom() {
		int count = (int) getCount();
		int rand = new Random().nextInt(count);
		return findAll(rand + 1, 1).iterator().next();
	}

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
			con = list.get(new Random().nextInt(list.size()));
		}
		return con;
	}

	private List<Connerie> getConneries(boolean spaces, String... keywords) {
		CriteriaQuery<Connerie> query = builder.createQuery(Connerie.class);
		Root<Connerie> root = query.from(Connerie.class);
		Path<String> value = root.get(Connerie_.value);

		List<Predicate> predicates = Lists.newArrayList();
		for (String keyword : keywords) {
			Predicate p = null;
			if (spaces) {
				p = builder.like(builder.lower(value),
						"% " + keyword.toLowerCase() + " %");
			} else {
				p = builder.like(builder.lower(value),
						"%" + keyword.toLowerCase() + "%");
			}
			predicates.add(p);
		}
		query.where(predicates.toArray(new Predicate[] {}));
		return em.createQuery(query).getResultList();
	}
}
