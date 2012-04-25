package be.hehehe.geekbot.persistence.dao;

import javax.inject.Named;

import be.hehehe.geekbot.persistence.model.RSSFeed;

import com.google.common.collect.Iterables;

@Named
public class RSSFeedDAO extends GenericDAO<RSSFeed> {
	public RSSFeed findByGUID(String guid) {
		return Iterables.getOnlyElement(findByField("guid", guid), null);
	}

	@Override
	protected Class<RSSFeed> getType() {
		return RSSFeed.class;
	}
}
