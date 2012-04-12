package be.hehehe.geekbot.persistence.dao;

import java.util.List;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import be.hehehe.geekbot.persistence.model.QuizzPlayer;

@Singleton
public class QuizzDAO extends GenericDAO<QuizzPlayer> {

	public void giveOnePoint(String author) {
		List<QuizzPlayer> players = findByField("name", author);
		if (players.isEmpty()) {
			QuizzPlayer player = new QuizzPlayer();
			player.setName(author);
			player.setPoints(0);
			save(player);
		} else {
			QuizzPlayer player = players.get(0);
			player.setPoints(player.getPoints() + 1);
			update(player);
		}
	}

	public List<QuizzPlayer> getPlayersOrderByPoints() {
		CriteriaQuery<QuizzPlayer> query = builder
				.createQuery(QuizzPlayer.class);
		Root<QuizzPlayer> root = query.from(QuizzPlayer.class);
		query.orderBy(builder.desc(root.get("points")));
		return em.createQuery(query).getResultList();
	}

}
