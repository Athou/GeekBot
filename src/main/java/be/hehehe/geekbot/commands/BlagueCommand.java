package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtils;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class BlagueCommand {

	@Trigger("!blague")
	public List<String> getRandomBlague() {
		List<String> toReturn = new ArrayList<String>();
		try {
			String url = "http://www.la-blague-du-jour.com/blagues_au_hasard/Une_blague_aleatoire.html";
			Document doc = Jsoup.parse(BotUtils.getContent(url));

			String cat = doc.select("p.Paratexte").select("b").select("a")
					.text();
			if (cat.startsWith("Monsieur et Madame")) {
				return getRandomBlague();
			}
			cat = cat.substring(0, cat.indexOf("("));

			String blague = doc.select("p.TexteBlague").html();
			List<String> lines = Arrays.asList(blague.split("<br />"));
			for (String line : lines) {
				toReturn.add(StringEscapeUtils.unescapeHtml(line));
			}

			if (toReturn.size() > 5) {
				// joke too long
				return getRandomBlague();
			}

			toReturn.add(0, IRCUtils.bold("Mega Vanne") + " - " + cat);
			toReturn.add(IRCUtils.bold("http://www.instantrimshot.com/"));
		} catch (Exception e) {
			LOG.handle(e);
			return getRandomBlague();
		}

		return toReturn;
	}
}
