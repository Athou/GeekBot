package be.hehehe.geekbot.persistence.dao;

import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.Connerie;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Stateless
public class ConnerieDAO extends GenericDAO<Connerie> {

	@Inject
	Random random;

	public Connerie getRandom() {
		return getRandom(-1);
	}

	public Connerie getRandom(int maxLength) {
		Connerie connerie = null;
		int count = (int) getCount(maxLength);
		if (count > 0) {
			int rand = random.nextInt(count) + 1;
			connerie = Iterables.getOnlyElement(findAll(rand, 1, maxLength),
					null);
		}
		return connerie;
	}

	public Connerie getRandomMatching(String... keywords) {
		return getRandomMatching(-1, keywords);
	}

	public Connerie getRandomMatching(int maxLength, String... keywords) {
		Connerie con = getConnerie(true, maxLength, keywords);
		if (con == null) {
			con = getConnerie(false, maxLength, keywords);
		}
		return con;
	}

	public int getCountMatching(String... keywords) {
		List<Connerie> list = getConneries(true, -1, keywords);
		if (list == null) {
			list = getConneries(false, -1, keywords);
		}

		return list.size();
	}

	private Connerie getConnerie(boolean spaces, int maxLength,
			String... keywords) {
		List<Connerie> list = getConneries(spaces, maxLength, keywords);
		Connerie con = null;
		if (!list.isEmpty()) {
			con = list.get(random.nextInt(list.size()));
		}
		return con;
	}

	private List<Connerie> getConneries(boolean spaces, int maxLength,
			String... keywords) {
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
		if (maxLength > 0) {
			predicates.add(builder.lessThan(builder.length(value), maxLength));
		}
		query.where(predicates.toArray(new Predicate[] {}));
		return em.createQuery(query).getResultList();
	}

	public long getCount(int maxLength) {
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Connerie> root = query.from(getType());
		Path<String> value = root.get("value");
		query.select(builder.count(root));
		if (maxLength > 0) {
			query.where(builder.lessThan(builder.length(value), maxLength));
		}
		return em.createQuery(query).getSingleResult();
	}

	public List<Connerie> findAll(int startIndex, int count, int maxLength) {
		CriteriaQuery<Connerie> query = builder.createQuery(getType());
		Root<Connerie> root = query.from(Connerie.class);
		Path<String> value = root.get("value");
		query.from(getType());
		if (maxLength > 0) {
			query.where(builder.lessThan(builder.length(value), maxLength));
		}
		TypedQuery<Connerie> q = em.createQuery(query);
		q.setMaxResults(count);
		q.setFirstResult(startIndex);
		return q.getResultList();
	}

	@Override
	protected Class<Connerie> getType() {
		return Connerie.class;
	}
}
