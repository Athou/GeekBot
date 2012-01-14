package be.hehehe.geekbot.persistence.dao;

import javax.inject.Singleton;

import be.hehehe.geekbot.persistence.model.RSSFeed;
import be.hehehe.geekbot.persistence.model.RSSFeed_;

import com.google.common.collect.Iterables;

@Singleton
public class RSSFeedDAO extends GenericDAO<RSSFeed> {
	public RSSFeed findByGUID(String guid) {
		return Iterables.getOnlyElement(findByField(RSSFeed_.guid, guid), null);
	}
}
