package be.hehehe.geekbot.commands;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;

import com.google.common.collect.Lists;

@BotCommand
public class GW2Command {

	private static final String URL = "http://mos.millenium.org/matchups/";

	@Inject
	BotUtilsService utilsService;

	@Trigger("!rvr")
	public List<String> millenium() {
		List<String> list = Lists.newArrayList();

		String content = utilsService.getContent(URL);
		Document document = Jsoup.parse(content);
		Element table = document.select("#EU table").first();
		Elements rows = table.select("tr");

		for (int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			if (row.text().contains("Vizunah Square")) {
				handleRow(list, row);
				break;
			}
		}

		return list;
	}

	private void handleRow(List<String> list, Element tr) {
		List<StringBuilder> strings = Arrays.asList(new StringBuilder(),
				new StringBuilder(), new StringBuilder());
		Elements tds = tr.select("td");

		// #
		Element td = tds.get(0);
		List<TextNode> indexes = td.textNodes();
		for (int i = 0; i < 3; i++) {
			strings.get(i).append(indexes.get(i));
		}

		// Server name
		td = tds.get(1);
		Elements serverNames = td.select("a");
		for (int i = 0; i < 3; i++) {
			strings.get(i).append(serverNames.get(i).text());
			strings.get(i).append(" ");
		}

		// current score
		td = tds.get(2);
		Elements scores = td.select("b");
		for (int i = 0; i < 3; i++) {
			strings.get(i).append(scores.get(i).text().replace(" ", "."));
			strings.get(i).append(" ");
		}

		// income
		td = tds.get(3);
		List<TextNode> incomes = td.textNodes();
		for (int i = 0; i < 3; i++) {
			strings.get(i).append("(");
			strings.get(i).append(incomes.get(i).toString().trim());
			strings.get(i).append(") ");
		}

		for (StringBuilder sb : strings) {
			list.add(sb.toString());
		}

		// last updated
		td = tds.get(7);
		Elements select = td.select("small");
		StringBuilder last = new StringBuilder();
		last.append("-- last updated: ");
		last.append(select.text().substring(select.text().indexOf(':') + 1)
				.trim());
		last.append(" --");
		list.add(last.toString());

	}
}
