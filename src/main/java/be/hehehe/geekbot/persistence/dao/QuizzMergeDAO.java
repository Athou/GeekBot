package be.hehehe.geekbot.persistence.dao;

import javax.inject.Inject;
import javax.inject.Named;

import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;

import com.google.common.collect.Iterables;

@Named
public class QuizzMergeDAO extends GenericDAO<QuizzMergeRequest> {

	@Inject
	QuizzDAO quizzDao;

	public void add(String nick1, String nick2) throws QuizzMergeException {
		QuizzPlayer player1 = Iterables.getOnlyElement(
				quizzDao.findByField("name", nick1), null);
		QuizzPlayer player2 = Iterables.getOnlyElement(
				quizzDao.findByField("name", nick2), null);

		if (player1 == null) {
			throw new QuizzMergeException(nick1 + ": player not found");
		}
		if (player2 == null) {
			throw new QuizzMergeException(nick2 + ": player not found");
		}

		QuizzMergeRequest request = new QuizzMergeRequest();
		request.setPlayer1(player1);
		request.setPlayer2(player2);
		save(request);

	}

	public void executeMerge(Long id) {
		QuizzMergeRequest request = findById(id);

		QuizzPlayer player1 = request.getPlayer1();
		QuizzPlayer player2 = request.getPlayer2();

		player1.setPoints(player1.getPoints() + player2.getPoints());
		quizzDao.delete(player2);
		quizzDao.update(player1);

		delete(request);

	}

	@Override
	protected Class<QuizzMergeRequest> getType() {
		return QuizzMergeRequest.class;
	}
}
