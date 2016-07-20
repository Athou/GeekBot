package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Fetches a random blague from la-blague-du-jour.com (French)
 * 
 * 
 */
@BotCommand
public class BlagueCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger("!blague")
	@Help("Fetches a random blague from humour-blague.com")
	public List<String> getRandomBlague() {
		List<String> result = new ArrayList<>();
		String url = "http://humour-blague.com/blagues-2/index.php";
		try {
			Document doc = Jsoup.parse(utilsService.getContent(url));

			List<TextNode> nodes = doc.select(".blague").get(0).textNodes();
			for (TextNode node : nodes) {
				result.add(StringEscapeUtils.unescapeHtml4(node.text()));
			}

			if (result.size() > 7) {
				// joke too long
				return getRandomBlague();
			}

			result.add(0, IRCUtils.bold("Mega Vanne"));
			result.add(IRCUtils.bold("http://www.instantrimshot.com/"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.add("Could not contact " + url);
		}

		return result;
	}
}
