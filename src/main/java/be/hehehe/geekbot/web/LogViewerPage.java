package be.hehehe.geekbot.web;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import be.hehehe.geekbot.persistence.dao.LogFileDAO;

@SuppressWarnings("serial")
public class LogViewerPage extends TemplatePage {

	private Level selectedLevel = Level.ERROR;

	@Inject
	LogFileDAO logDAO;

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

		IModel<String> logModel = new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				List<String> filteredLines = null;
				try {
					filteredLines = logDAO.getLines(selectedLevel);
					if (filteredLines.isEmpty()) {
						return "Nothing to display";
					}
				} catch (IOException e) {
					return "Could not read log file: " + e.getMessage();
				}
				return StringUtils.join(filteredLines,
						SystemUtils.LINE_SEPARATOR);
			}
		};

		add(new Label("logwrapper", logModel));

	}

	@Override
	protected String getTitle() {
		return "Latest Logs";
	}

}
