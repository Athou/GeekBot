package be.hehehe.geekbot.persistence.dao;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.Skandite;
import be.hehehe.geekbot.persistence.model.Skandite_;

import com.google.common.collect.Iterables;

@Singleton
public class SkanditeDAO extends GenericDAO<Skandite> {

	public Skandite findByURL(String url) {
		return Iterables.getOnlyElement(findByField(Skandite_.url, url), null);
	}

	public Skandite findByHashAndByteCount(String hash, Long byteCount) {

		CriteriaQuery<Skandite> query = builder.createQuery(Skandite.class);
		Root<Skandite> root = query.from(Skandite.class);
		Predicate condition1 = builder.equal(root.get(Skandite_.hash), hash);
		Predicate condition2 = builder.equal(root.get(Skandite_.byteCount),
				byteCount);
		query.where(builder.and(condition1, condition2));
		return Iterables.getOnlyElement(em.createQuery(query).getResultList(),
				null);
	}
}
