package be.hehehe.geekbot.commands;

import java.util.Arrays;
import java.util.Collections;
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
		List<ServerScore> serverScores = Arrays.asList(new ServerScore(),
				new ServerScore(), new ServerScore());
		Elements tds = tr.select("td");

		// rank
		Element td = tds.get(1);
		Elements ranks = td.select("span.badge");
		for (int i = 0; i < 3; i++) {
			serverScores.get(i).setRank(
					Integer.parseInt(ranks.get(i).ownText()));
		}

		// Server name
		Elements serverNames = td.select("a");
		for (int i = 0; i < 3; i++) {
			serverScores.get(i).setName(serverNames.get(i).text().trim());
		}

		// current score
		td = tds.get(2);
		Elements scores = td.select("b");
		for (int i = 0; i < 3; i++) {
			serverScores.get(i).setScore(
					scores.get(i).text().replace(" ", ".").trim());
		}

		// income
		td = tds.get(3);
		List<TextNode> incomes = td.textNodes();
		for (int i = 0; i < 3; i++) {
			serverScores.get(i).setIncome(incomes.get(i).toString().trim());
		}

		for (ServerScore score : serverScores) {
			list.add(score.toString());
		}

		Collections.sort(list);

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

	private class ServerScore implements Comparable<ServerScore> {
		private int rank;
		private String name;
		private String score;
		private String income;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(rank);
			sb.append(". ");
			sb.append(name);
			sb.append(" ");
			sb.append(score);
			sb.append(" (");
			sb.append(income);
			sb.append(")");
			return sb.toString();
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public void setIncome(String income) {
			this.income = income;
		}

		@Override
		public int compareTo(ServerScore o) {
			return rank - o.rank;
		}

	}
}
