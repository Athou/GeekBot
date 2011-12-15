package be.hehehe.geekbot.persistence.dao;

import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;

import be.hehehe.geekbot.persistence.model.Skandite;

@Singleton
public class SkanditeDAO extends GenericDAO<Skandite> {

	public Skandite findByURL(String url) {
		Object result = createCriteria().add(Restrictions.eq("url", url))
				.uniqueResult();
		return ((Skandite) result);
	}

	public Skandite findByHashAndByteCount(String hash, Long byteCount) {
		Object result = createCriteria().add(Restrictions.eq("hash", hash))
				.add(Restrictions.eq("byteCount", byteCount)).uniqueResult();
		return ((Skandite) result);
	}
}
