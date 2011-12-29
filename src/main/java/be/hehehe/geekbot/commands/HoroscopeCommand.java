package be.hehehe.geekbot.commands;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jibble.jmegahal.JMegaHal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

/**
 * Horoscope from astrocenter.fr (French)
 * 
 */
@BotCommand
public class HoroscopeCommand {

	private static final Map<String, String> mapping = new HashMap<String, String>();
	@Inject
	BotUtilsService utilsService;

	static {
		mapping.put("belier", "0");
		mapping.put("taureau", "1");
		mapping.put("gemeaux", "2");
		mapping.put("cancer", "3");
		mapping.put("lion", "4");
		mapping.put("vierge", "5");
		mapping.put("balance", "6");
		mapping.put("scorpion", "7");
		mapping.put("sagittaire", "8");
		mapping.put("capricorne", "9");
		mapping.put("verseau", "10");
		mapping.put("poissons", "11");

	}

	@Trigger(value = "!horoscope", type = TriggerType.EXACTMATCH)
	public String getHoroscopeHelp() {
		return IRCUtils.bold("!horoscope <signe>")
				+ " - Available signs : belier, taureau, gemeaux, cancer, lion, vierge, balance, scorpion, sagittaire, capricorne, verseau, poisson, random";
	}

	@Trigger(value = "!horoscope", type = TriggerType.STARTSWITH)
	public String getHoroscope(TriggerEvent event) {
		String sign = event.getMessage();
		if ("poisson".equals(sign)) {
			sign = "poissons";
		}

		String line = null;
		try {
			Document doc = Jsoup
					.parse(utilsService
							.getContent("http://www.astrocenter.fr/fr/FCDefault.aspx?Af=0"));
			if ("random".equals(sign)) {
				line = getGenerated(doc);
			} else {
				line = getLineFor(doc, mapping.get(sign));
			}

		} catch (Exception e) {
			LOG.handle(e);
		}

		return line;
	}

	private String getGenerated(Document doc) {
		JMegaHal hal = new JMegaHal();
		for (String id : mapping.values()) {
			String line = getLineFor(doc, id);
			for (String sentence : line.split("\\.")) {
				hal.add(sentence.trim());
			}
		}

		String result = "";
		for (int i = 0; i < 3; i++) {
			String generated = hal.getSentence();
			if (Character.isLetter(generated.charAt(generated.length() - 1))) {
				generated += ".";
			}
			generated += " ";
			result += generated;
		}

		return result;
	}

	private String getLineFor(Document doc, String id) {
		Element horo = doc.select("div#ast-sign-" + id).first();
		horo = horo.select(".ast-description p").first();
		return horo.text();
	}
}