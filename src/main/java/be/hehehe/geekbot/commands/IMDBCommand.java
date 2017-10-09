package be.hehehe.geekbot.commands;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.commands.GoogleCommand.Mode;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * IMDB commands
 * 
 */
@BotCommand
@JBossLog
public class IMDBCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	GoogleCommand googleCommand;

	@Trigger(value = "!imdb", type = TriggerType.STARTSWITH)
	@Help("IMDb Movie search.")
	public List<String> getResult(TriggerEvent event) {
		String keywords = event.getMessage();
		List<String> googleResult = googleCommand.google("site:http://www.imdb.com/title " + keywords, Mode.WEB);
		String[] split = googleResult.get(0).split("/");
		String imdbID = null;

		for (String s : split) {
			if (s.startsWith("tt")) {
				imdbID = s;
				break;
			}
		}

		String result = utilsService.getContent("http://app.imdb.com/title/maindetails?tconst=" + imdbID);
		return parse(result);
	}

	private List<String> parse(String source) {
		List<String> result = new ArrayList<>();

		try {
			JSONObject json = new JSONObject(source).getJSONObject("data");

			String title = json.getString("title");
			String url = "http://www.imdb.com/title/" + json.getString("tconst") + "/";
			String year = json.getString("year");

			String s = IRCUtils.bold(title);
			if (!"null".equals(year)) {
				s += " (" + year + ")";
			}

			s += " - " + URLDecoder.decode(url, "UTF-8");
			result.add(s);
			s = "";

			if (!json.isNull("tagline")) {
				String plot = json.getString("tagline");
				result.add(IRCUtils.bold("Plot: ") + plot);
			}
			s = "";
			if (!json.isNull("rating") && !json.isNull("num_votes")) {
				String rating = json.getString("rating");
				String votes = json.getString("num_votes");
				s = IRCUtils.bold("Rating: ") + rating + "/10 - " + votes + " votes - ";
			}

			if (!json.isNull("genres")) {
				List<String> genres = new ArrayList<>();
				JSONArray genresArray = json.getJSONArray("genres");
				for (int i = 0; i < genresArray.length(); i++) {
					String genre = (String) genresArray.get(i);
					genres.add(genre);

				}
				s += StringUtils.join(genres, ", ");
			}
			result.add(s);
			s = "";
			if (!json.isNull("directors_summary")) {
				List<String> directors = new ArrayList<>();

				for (int i = 0; i < json.getJSONArray("directors_summary").length(); i++) {
					JSONObject j2 = (JSONObject) json.getJSONArray("directors_summary").get(i);
					directors.add(j2.getJSONObject("name").getString("name"));

				}
				s = IRCUtils.bold("Directed by: ");
				for (String director : directors) {
					s += director + ", ";
				}
				s = s.substring(0, s.length() - 2);
				result.add(s);
			}
			s = "";
			if (!json.isNull("cast_summary")) {

				List<String> actors = new ArrayList<>();

				for (int i = 0; i < json.getJSONArray("cast_summary").length() && i < 5; i++) {
					JSONObject j2 = (JSONObject) json.getJSONArray("cast_summary").get(i);
					actors.add(j2.getJSONObject("name").getString("name"));

				}
				s = IRCUtils.bold("Actors: ");
				for (String actor : actors) {
					s += actor + ", ";
				}
				s = s.substring(0, s.length() - 2);
				result.add(s);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		List<String> result2 = new ArrayList<>();
		for (String s : result) {
			result2.add(StringEscapeUtils.unescapeHtml4(s));
		}

		return result2;
	}
}
