package be.hehehe.geekbot.persistence.dao;

import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.Connerie;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Stateless
public class ConnerieDAO extends GenericDAO<Connerie> {

	public Connerie getRandom() {
		Connerie connerie = null;
		int count = (int) getCount();
		if (count > 0) {
			int rand = new Random().nextInt(count) + 1;
			connerie = Iterables.getOnlyElement(findAll(rand, 1), null);
		}
		return connerie;
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
		Path<String> value = root.get("value");

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

	@Override
	protected Class<Connerie> getType() {
		return Connerie.class;
	}
}
