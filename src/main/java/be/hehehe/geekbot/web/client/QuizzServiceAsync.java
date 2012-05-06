package be.hehehe.geekbot.web.client;

import java.util.List;

import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuizzServiceAsync {

	void getPlayers(AsyncCallback<List<QuizzPlayer>> callback);

	void getRequests(AsyncCallback<List<QuizzMergeRequest>> callback);

	void addMergeRequest(String player1, String player2,
			AsyncCallback<Void> callback) throws QuizzMergeException;

	void acceptMergeRequest(String password, Long requestId,
			AsyncCallback<Void> callback);

	void denyMergeRequest(String password, Long requestId,
			AsyncCallback<Void> callback);

}
