package be.hehehe.geekbot.commands;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Finds out the next air date of tv shows.
 * 
 */
@BotCommand
public class EpisodesCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger(value = "!next", type = TriggerType.STARTSWITH)
	@Help("Information about a TV show from TVRage.com")
	public List<String> getNextEpisode(TriggerEvent event) {
		String seriesName = event.getMessage();

		List<String> list = new ArrayList<String>();
		try {
			String url = String.format("http://api.tvmaze.com/singlesearch/shows?q=%s&embed=episodes",
					URLEncoder.encode(seriesName, "UTF-8"));
			String content = utilsService.getContent(url);
			JSONObject root = new JSONObject(content);
			JSONObject embedded = root.getJSONObject("_embedded");
			JSONArray episodes = embedded.getJSONArray("episodes");

			JSONObject previous = null;
			JSONObject next = null;
			Date now = new Date();
			for (int i = 0; i < episodes.length(); i++) {
				JSONObject episode = episodes.getJSONObject(i);
				Date airdate = parseAirDate(episode);
				if (airdate != null) {
					if (airdate.before(now)) {
						previous = episode;
					} else if (next == null) {
						next = episode;
					}
				}
			}

			list.add(IRCUtils.bold("Show Name: ") + root.getString("name"));
			if (next != null) {
				list.add(IRCUtils.bold("Next Episode: ") + parseEpisode(next));
			} else {
				list.add(IRCUtils.bold("Next Episode: ") + "N/A");
			}
			if (previous != null) {
				list.add(IRCUtils.bold("Previous Episode: ") + parseEpisode(next));
			} else {
				list.add(IRCUtils.bold("Previous Episode: ") + "N/A");
			}
			list.add(IRCUtils.bold("Show URL: http://www.imdb.com/title/") + root.getJSONObject("externals").getString("imdb"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return list;
	}

	public String parseEpisode(JSONObject episode) throws JSONException, ParseException {
		List<String> parts = new ArrayList<String>();
		parts.add(episode.getString("name"));

		Date airdate = parseAirDate(episode);
		parts.add("(" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(airdate));

		PrettyTime prettyTime = new PrettyTime(Locale.FRENCH);
		parts.add(", " + prettyTime.format(airdate) + ")");

		return StringUtils.join(parts, " ");
	}

	private Date parseAirDate(JSONObject episode) throws JSONException, ParseException {
		String airstamp = episode.getString("airstamp");
		if (airstamp == null)
			return null;

		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(airstamp);
	}

}
