package be.hehehe.geekbot.web.client;

import java.util.List;

import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.web.client.Quizz.RefreshEvent;
import be.hehehe.geekbot.web.client.Quizz.RefreshHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class QuizzMergeRequestsPanel extends FlowPanel implements
		RefreshHandler {

	private FlexTable requestsTable;
	private PasswordTextBox password;
	private HorizontalPanel passwordPanel;

	private final QuizzServiceAsync quizzService = GWT
			.create(QuizzService.class);

	public QuizzMergeRequestsPanel() {
		super();

		Quizz.eventBus.addHandler(RefreshEvent.TYPE, this);

		requestsTable = new FlexTable();
		requestsTable.setStyleName("center");

		password = new PasswordTextBox();

		add(requestsTable);

		passwordPanel = new HorizontalPanel();
		passwordPanel.setStyleName("center");
		passwordPanel.setVisible(false);
		passwordPanel.add(new Label("Password: "));
		passwordPanel.add(password);
		add(passwordPanel);

		refreshRequests();
	}

	private void refreshRequests() {
		quizzService.getRequests(new AsyncCallback<List<QuizzMergeRequest>>() {

			@Override
			public void onSuccess(List<QuizzMergeRequest> list) {
				populateRequests(list);
			}

			@Override
			public void onFailure(Throwable t) {
				requestsTable.removeAllRows();
				requestsTable.setText(0, 0, t.getMessage());
			}
		});

	}

	private void populateRequests(final List<QuizzMergeRequest> requests) {
		requestsTable.removeAllRows();

		passwordPanel.setVisible(!requests.isEmpty());
		if (requests.isEmpty()) {
			requestsTable.setText(0, 0, "No requests.");
		} else {
			requestsTable.setText(0, 0, "Receiving Player");
			requestsTable.setText(0, 1, "Giving Player");
			for (int i = 0; i < requests.size(); i++) {
				final QuizzMergeRequest request = requests.get(i);

				FocusPanel accept = new FocusPanel(new Image(
						"images/accept.png"));
				accept.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						quizzService.acceptMergeRequest(password.getValue(),
								request.getId(), new AsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										Quizz.eventBus
												.fireEvent(new RefreshEvent());
									}

									@Override
									public void onFailure(Throwable caught) {

									}

								});
					}
				});
				accept.addStyleName("pointer");

				FocusPanel deny = new FocusPanel(new Image("images/cross.png"));
				deny.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						quizzService.denyMergeRequest(password.getValue(),
								request.getId(), new AsyncCallback<Void>() {

									@Override
									public void onSuccess(Void result) {
										Quizz.eventBus
												.fireEvent(new RefreshEvent());
									}

									@Override
									public void onFailure(Throwable caught) {

									}
								});
					}
				});
				deny.addStyleName("pointer");

				requestsTable.setText(i + 1, 0, request.getPlayer1().getName());
				requestsTable.setText(i + 1, 1, request.getPlayer2().getName());
				requestsTable.setWidget(i + 1, 2, accept);
				requestsTable.setWidget(i + 1, 3, deny);
			}
		}
	}

	@Override
	public void onRefreshNeeded(RefreshEvent refreshEvent) {
		refreshRequests();
	}
}
