package be.hehehe.geekbot.web.server;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.GWTServlet;
import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.persistence.dao.QuizzMergeDAO;
import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.web.client.QuizzService;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
@GWTServlet(path = "/Quizz/quizz")
public class QuizzServiceImpl extends RemoteServiceServlet implements
		QuizzService {

	@SuppressWarnings("unchecked")
	public <T> List<T> copy(List<T> list) {
		List<T> newList = Lists.newArrayList();
		try {
			for (T t : list) {
				newList.add((T) BeanUtils.cloneBean(t));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return newList;
	}

	@Inject
	QuizzDAO quizzDAO;

	@Inject
	QuizzMergeDAO quizzMergeDAO;

	@Inject
	BundleService bundleService;

	@Override
	public List<QuizzPlayer> getPlayers() {
		return copy(quizzDAO.getPlayersOrderByPoints());

	}

	@Override
	public List<QuizzMergeRequest> getRequests() {
		return copy(quizzMergeDAO.findAll());
	}

	@Override
	public void addMergeRequest(String player1, String player2)
			throws QuizzMergeException {
		quizzMergeDAO.add(player1, player2);
	}

	@Override
	public void acceptMergeRequest(String password, Long requestId) {
		String adminPassword = bundleService.getAdminPassword();
		if (StringUtils.equals(adminPassword, password)) {
			quizzMergeDAO.executeMerge(requestId);
		}

	}

	@Override
	public void denyMergeRequest(String password, Long requestId) {
		String adminPassword = bundleService.getAdminPassword();
		if (StringUtils.equals(adminPassword, password)) {
			quizzMergeDAO.deleteById(requestId);
		}
	}

}
