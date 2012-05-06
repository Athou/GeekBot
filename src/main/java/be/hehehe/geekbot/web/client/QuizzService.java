package be.hehehe.geekbot.web.client;

import java.util.List;

import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("quizz")
public interface QuizzService extends RemoteService {
	List<QuizzPlayer> getPlayers();

	List<QuizzMergeRequest> getRequests();

	void addMergeRequest(String player1, String player2)
			throws QuizzMergeException;

	void acceptMergeRequest(String password, Long requestId);

	void denyMergeRequest(String password, Long requestId);
}
