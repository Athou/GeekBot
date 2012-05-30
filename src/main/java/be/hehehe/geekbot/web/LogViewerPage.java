package be.hehehe.geekbot.web;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class LogViewerPage extends TemplatePage {

	private static final List<Level> LEVELS = Lists.newArrayList(Level.ALL,
			Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR);

	private Level selectedLevel = Level.ALL;

	public LogViewerPage() {

		DropDownChoice<Level> levelsChoice = new DropDownChoice<Level>(
				"levels", new PropertyModel<Level>(this, "selectedLevel"),
				LEVELS) {
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

		add(new Label("log", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				List<String> lines = null;
				try {
					lines = FileUtils.readLines(new File("geekbot.log"));
					if (lines.isEmpty()) {
						return "Log file is empty";
					}
				} catch (IOException e) {
					return "Could not load log content: " + e.getMessage();
				}

				List<String> filteredLines = Lists.newArrayList();
				boolean lastLineWasAdded = false;
				for (String line : lines) {
					Level level = toLevel(line.substring(0, line.indexOf(" ")));
					if (level == null && lastLineWasAdded) {
						filteredLines.add(line);
						lastLineWasAdded = true;
					} else if (level != null
							&& (selectedLevel == Level.ALL || level
									.isGreaterOrEqual(selectedLevel))) {
						filteredLines.add(line);
						lastLineWasAdded = true;
					} else {
						lastLineWasAdded = false;
					}
				}
				return StringUtils.join(filteredLines,
						SystemUtils.LINE_SEPARATOR);
			}
		}));
	}

	private Level toLevel(String levelName) {
		for (Level level : LEVELS) {
			if (StringUtils.equals(levelName, level.toString())) {
				return level;
			}
		}
		return null;
	}

	@Override
	protected String getTitle() {
		return "Latest Logs";
	}

}
