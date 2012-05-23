package be.hehehe.geekbot.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.basic.Label;

@SuppressWarnings("serial")
public class LogViewerPage extends TemplatePage {

	public LogViewerPage() {
		String logContent = null;
		try {
			logContent = FileUtils.readFileToString(new File("geekbot.log"));
		} catch (IOException e) {
			logContent = "Could not load log file content: " + e.getMessage();
		}
		add(new Label("log", logContent));
	}

	@Override
	protected String getTitle() {
		return "Latest Logs";
	}

}
