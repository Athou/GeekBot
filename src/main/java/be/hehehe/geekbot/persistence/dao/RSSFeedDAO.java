package be.hehehe.geekbot.persistence.dao;

import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import be.hehehe.geekbot.persistence.model.RSSFeed;

@Singleton
public class RSSFeedDAO extends GenericDAO<RSSFeed> {
	public RSSFeed findByGUID(String guid) {
		Criteria crit = createCriteria();
		crit.add(Restrictions.eq("guid", guid));
		return (RSSFeed) crit.uniqueResult();
	}
}
