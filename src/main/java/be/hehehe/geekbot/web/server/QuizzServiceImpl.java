package be.hehehe.geekbot.web.server;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.Main;
import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.persistence.dao.QuizzMergeDAO;
import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.web.client.QuizzService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class QuizzServiceImpl extends RemoteServiceServlet implements
		QuizzService {

	@Override
	public List<QuizzPlayer> getPlayers() {
		return Main.getInstance().select(QuizzDAO.class).get()
				.getPlayersOrderByPoints();

	}

	@Override
	public List<QuizzMergeRequest> getRequests() {
		return Main.getInstance().select(QuizzMergeDAO.class).get().findAll();
	}

	@Override
	public void addMergeRequest(String player1, String player2)
			throws QuizzMergeException {
		Main.getInstance().select(QuizzMergeDAO.class).get()
				.add(player1, player2);
	}

	@Override
	public void acceptMergeRequest(String password, Long requestId) {
		String adminPassword = Main.getInstance().select(BundleService.class)
				.get().getAdminPassword();
		if (StringUtils.equals(adminPassword, password)) {
			Main.getInstance().select(QuizzMergeDAO.class).get()
					.executeMerge(requestId);
		}

	}

	@Override
	public void denyMergeRequest(String password, Long requestId) {
		String adminPassword = Main.getInstance().select(BundleService.class)
				.get().getAdminPassword();
		if (StringUtils.equals(adminPassword, password)) {
			Main.getInstance().select(QuizzMergeDAO.class).get()
					.deleteById(requestId);
		}
	}

}
