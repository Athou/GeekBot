package be.hehehe.geekbot.web.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
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

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception {
		Object result = ctx.proceed();
		if (result != null) {
			if (result instanceof List) {
				List<?> oldList = (List<?>) result;
				List<Object> newList = Lists.newArrayList();
				for (Object o : oldList) {
					newList.add(BeanUtils.cloneBean(o));
				}
				result = newList;
			} else {
				result = BeanUtils.cloneBean(result);
			}
		}
		return result;
	}

	@Inject
	QuizzDAO quizzDAO;

	@Inject
	QuizzMergeDAO quizzMergeDAO;

	@Inject
	BundleService bundleService;

	@Override
	public List<QuizzPlayer> getPlayers() {
		return quizzDAO.getPlayersOrderByPoints();

	}

	@Override
	public List<QuizzMergeRequest> getRequests() {
		return quizzMergeDAO.findAll();
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
