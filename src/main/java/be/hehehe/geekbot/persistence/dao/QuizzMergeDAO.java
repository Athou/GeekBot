package be.hehehe.geekbot.persistence.dao;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;

import com.google.common.collect.Iterables;

@Stateless
public class QuizzMergeDAO extends GenericDAO<QuizzMergeRequest> {

	@Inject
	QuizzDAO quizzDao;

	public void add(String receiver, String giver) throws QuizzMergeException {

		if (StringUtils.isBlank(receiver) || StringUtils.isBlank(giver)) {
			throw new QuizzMergeException("Both players are required.");
		}

		if (StringUtils.equals(receiver, giver)) {
			throw new QuizzMergeException("Players need to be different.");
		}

		QuizzPlayer receivingPlayer = Iterables.getOnlyElement(
				quizzDao.findByField("name", receiver), null);
		QuizzPlayer givingPlayer = Iterables.getOnlyElement(
				quizzDao.findByField("name", giver), null);

		if (receivingPlayer == null) {
			throw new QuizzMergeException("Player not found: " + receiver);
		}
		if (givingPlayer == null) {
			throw new QuizzMergeException("Player not found: " + giver);
		}

		QuizzMergeRequest request = new QuizzMergeRequest();
		request.setGiver(giver);
		request.setReceiver(receiver);
		save(request);

	}

	public void executeMerge(Long id) {
		QuizzMergeRequest request = findById(id);

		QuizzPlayer receivingPlayer = Iterables.getOnlyElement(
				quizzDao.findByField("name", request.getReceiver()), null);
		QuizzPlayer givingPlayer = Iterables.getOnlyElement(
				quizzDao.findByField("name", request.getGiver()), null);

		delete(request);
		receivingPlayer.setPoints(receivingPlayer.getPoints()
				+ givingPlayer.getPoints());
		quizzDao.delete(givingPlayer);
		quizzDao.update(receivingPlayer);

	}

	@Override
	protected Class<QuizzMergeRequest> getType() {
		return QuizzMergeRequest.class;
	}
}
