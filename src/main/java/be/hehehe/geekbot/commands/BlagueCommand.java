package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

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
			String result = BotUtils.getContent(url);
			String cat = result
					.substring(result
							.indexOf("Voici une blague au hasard de la catégorie : ") + 50);
			cat = cat.substring(cat.indexOf(">") + 1);
			if (cat.startsWith("Monsieur et Madame")) {
				return getRandomBlague();
			}

			cat = cat.substring(0, cat.indexOf("("));

			result = result
					.substring(result.indexOf("<p class='TexteBlague'>") + 23);
			result = result.substring(0, result.indexOf("</p>"));
			List<String> lines = Arrays.asList(result.split("<br>"));

			for (String s : lines) {
				String toAdd = StringEscapeUtils.unescapeHtml(
						s.replaceAll("\\<.*?\\>", "")).replaceAll("\\s+", " ");
				while (toAdd.length() > 400) {
					toReturn.add(toAdd.substring(0, 400));
					toAdd = toAdd.substring(400);
				}
				toReturn.add(toAdd);
			}
			if (toReturn.size() > 5) {
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
