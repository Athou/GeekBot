package be.hehehe.geekbot.commands;

import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

/**
 * Finds out the next air date of tv shows.
 * 
 */
@BotCommand
public class EpisodesCommand {

	@Inject
	BotUtilsService utilsService;

	@Trigger(value = "!next", type = TriggerType.STARTSWITH)
	public List<String> getNextEpisode(TriggerEvent event) {
		String seriesName = event.getMessage();
		Map<String, String> showInfos = new HashMap<String, String>();
		try {
			String url = "http://services.tvrage.com/tools/quickinfo.php?show="
					+ URLEncoder.encode(seriesName, "UTF-8");
			for (String line : IOUtils.readLines(new StringReader(utilsService
					.getContent(url)))) {
				String[] split = line.split("@");
				if (split.length == 2) {
					showInfos.put(split[0], split[1]);
				}
			}
		} catch (Exception e) {
			LOG.handle(e);
		}
		return buildStrings(showInfos);
	}

	private List<String> buildStrings(Map<String, String> showInfos) {
		List<String> list = new ArrayList<String>();
		if (showInfos.get("Show Name") != null) {
			list.add(IRCUtils.bold("Show Name: ") + showInfos.get("Show Name"));
		}
		if (showInfos.get("Next Episode") != null) {
			list.add(IRCUtils.bold("Next Episode: ")
					+ parseEpisode(showInfos.get("Next Episode")));
		} else {
			list.add(IRCUtils.bold("Next Episode: ") + "N/A");
		}

		if (showInfos.get("Latest Episode") != null) {
			list.add(IRCUtils.bold("Latest Episode: ")
					+ parseEpisode(showInfos.get("Latest Episode")));
		}
		if (showInfos.get("Show URL") != null) {
			list.add(IRCUtils.bold("Show URL: ") + showInfos.get("Show URL"));
		}

		return list;
	}

	public static String parseEpisode(String s) {
		String result = s.replaceAll("\\^", " ");
		try {
			String[] split = result.split(" ");
			String dateString = split[split.length - 1];
			Date date = new SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
					.parse(dateString);
			dateString = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
					.format(date);
			split[split.length - 1] = " - " + dateString;
			result = StringUtils.join(split, " ");

			date = DateUtils.truncate(date, Calendar.DATE);
			Date now = DateUtils.truncate(new Date(), Calendar.DATE);
			long diff = date.getTime() - now.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			String add = "";
			if (diffDays == 0) {
				add = " (today)";
			} else if (diffDays == 1) {
				add = " (tomorrow)";
			} else if (diffDays > 1) {
				add = " (" + diffDays + " days from now)";
			} else if (diffDays == -1) {
				add = " (yesterday)";
			} else if (diffDays < -1) {
				diffDays = -diffDays;
				add = " (" + diffDays + " days ago)";
			}
			result += add;
		} catch (Exception e) {
			LOG.handle(e);
			result = s.replaceAll("\\^", " ");
		}
		return result;

	}
}
