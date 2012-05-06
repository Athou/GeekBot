package be.hehehe.geekbot.web.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;
import be.hehehe.geekbot.web.client.Quizz.RefreshEvent;
import be.hehehe.geekbot.web.client.Quizz.RefreshHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class QuizzPlayersPanel extends FlowPanel implements RefreshHandler {

	private FlexTable playersTable;
	private Map<QuizzPlayer, CheckBox> checkBoxes;
	private Button submitButton;

	private final QuizzServiceAsync quizzService = GWT
			.create(QuizzService.class);

	public QuizzPlayersPanel() {
		super();

		Quizz.eventBus.addHandler(RefreshEvent.TYPE, this);

		playersTable = new FlexTable();
		playersTable.setStyleName("center");

		checkBoxes = new HashMap<QuizzPlayer, CheckBox>();
		submitButton = new Button("Add Merge Request");
		submitButton.setEnabled(false);
		submitButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				QuizzPlayer player1 = null;
				QuizzPlayer player2 = null;
				for (QuizzPlayer player : checkBoxes.keySet()) {
					CheckBox box = checkBoxes.get(player);
					if (box.getValue()) {
						if (player1 == null) {
							player1 = player;
						} else {
							player2 = player;
							break;
						}
					}
				}
				if (player1 != null && player2 != null) {
					try {
						quizzService.addMergeRequest(player1.getName(),
								player2.getName(), new AsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										Quizz.eventBus
												.fireEvent(new RefreshEvent());
									}

									@Override
									public void onFailure(Throwable caught) {
										addMergeRequestFailed(caught
												.getMessage());
									}

								});
					} catch (QuizzMergeException e) {
						addMergeRequestFailed(e.getMessage());
					}
				}
			}
		});

		refreshPlayers();
		add(playersTable);
	}

	private void refreshPlayers() {
		quizzService.getPlayers(new AsyncCallback<List<QuizzPlayer>>() {

			@Override
			public void onSuccess(List<QuizzPlayer> list) {
				populatePlayers(list);
			}

			@Override
			public void onFailure(Throwable t) {
				playersTable.removeAllRows();
				playersTable.setText(0, 0,
						"Error while fetching players list: " + t.getMessage());
				t.printStackTrace();
			}
		});

	}

	private void populatePlayers(List<QuizzPlayer> players) {
		playersTable.removeAllRows();
		checkBoxes.clear();

		playersTable.setText(0, 0, "Rank");
		playersTable.setText(0, 1, "Player Name");
		playersTable.setText(0, 2, "Points");
		playersTable.setText(0, 3, "Add Merge Request");
		for (int i = 0; i < players.size(); i++) {
			QuizzPlayer player = players.get(i);
			playersTable.setText(i + 1, 0, "" + (i + 1));
			playersTable.setText(i + 1, 1, player.getName());
			playersTable.setText(i + 1, 2, "" + player.getPoints());

			CheckBox checkBox = new CheckBox();
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					checkSubmitButtonStatus();
				}
			});
			playersTable.setWidget(i + 1, 3, checkBox);
			checkBoxes.put(player, checkBox);
		}

		playersTable.setWidget(players.size() + 1, 3, submitButton);
	}

	private void checkSubmitButtonStatus() {
		int selectedCount = 0;
		for (CheckBox checkBox : checkBoxes.values()) {
			if (checkBox.getValue()) {
				selectedCount++;
			}
		}
		submitButton.setEnabled(selectedCount == 2);
	}

	private void addMergeRequestFailed(String message) {
		add(new Label(message));

	}

	@Override
	public void onRefreshNeeded(RefreshEvent refreshEvent) {
		refreshPlayers();

	}
}
