package be.hehehe.geekbot.web;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import be.hehehe.geekbot.persistence.dao.LogFileDAO;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class LogViewerPage extends TemplatePage {

	private Level selectedLevel = Level.ALL;

	public LogViewerPage() {

		DropDownChoice<Level> levelsChoice = new DropDownChoice<Level>(
				"levels", new PropertyModel<Level>(this, "selectedLevel"),
				LogFileDAO.LEVELS) {
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

			@Override
			protected void onSelectionChanged(Level newSelection) {
				selectedLevel = newSelection;
			}

		};
		add(levelsChoice);

		IModel<List<String>> logModel = new LoadableDetachableModel<List<String>>() {
			@Override
			protected List<String> load() {
				List<String> filteredLines = null;
				try {
					filteredLines = getBean(LogFileDAO.class).getLines(
							selectedLevel);
					if (filteredLines.isEmpty()) {
						return Lists.newArrayList("Nothing to display");
					}
				} catch (IOException e) {
					return Lists.newArrayList("Could not read log file: "
							+ e.getMessage());
				}
				return filteredLines;
			}
		};

		add(new ListView<String>("logwrapper", logModel) {
			protected void populateItem(ListItem<String> item) {
				String logLine = item.getModelObject();
				item.add(new Label("logline", logLine));
			}
		});

	}

	@Override
	protected String getTitle() {
		return "Latest Logs";
	}

}
