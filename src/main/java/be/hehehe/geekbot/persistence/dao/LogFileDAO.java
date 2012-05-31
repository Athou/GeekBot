package be.hehehe.geekbot.persistence.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

import com.google.common.collect.Lists;

@Named
public class LogFileDAO {

	public static final List<Level> LEVELS = Lists.newArrayList(Level.ALL,
			Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR);

	public List<String> getLines() throws IOException {
		return FileUtils.readLines(new File("geekbot.log"));
	}

	public List<String> getLines(Level selectedLevel) throws IOException {
		List<String> lines = getLines();
		List<String> filteredLines = Lists.newArrayList();
		boolean lastLineWasAdded = false;
		for (String line : lines) {
			int indexOfSpace = line.indexOf(" ");
			if (indexOfSpace > 0) {
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
		}
		return filteredLines;
	}

	private Level toLevel(String levelName) {
		for (Level level : LEVELS) {
			if (StringUtils.equals(levelName, level.toString())) {
				return level;
			}
		}
		return null;
	}
}
