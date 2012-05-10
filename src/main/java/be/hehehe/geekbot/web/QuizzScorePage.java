package be.hehehe.geekbot.web;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;

@SuppressWarnings("serial")
public class QuizzScorePage extends TemplatePage {

	@Inject
	QuizzDAO quizzDAO;

	public QuizzScorePage() {

		IModel<List<QuizzPlayer>> model = new LoadableDetachableModel<List<QuizzPlayer>>() {
			@Override
			protected List<QuizzPlayer> load() {
				return quizzDAO.getPlayersOrderByPoints();
			}
		};

		ListView<QuizzPlayer> playersView = new PropertyListView<QuizzPlayer>(
				"players", model) {
			@Override
			protected void populateItem(ListItem<QuizzPlayer> item) {
				item.add(new Label("rank", "" + (item.getIndex() + 1)));
				item.add(new Label("name"));
				item.add(new Label("points"));
			}
		};

		add(playersView);
	}

	@Override
	protected String getTitle() {
		return "Quizz Scoreboard";
	}

}
