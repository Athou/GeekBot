package be.hehehe.geekbot.commands;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Maps;

/**
 * Horoscope from astrocenter.fr (French)
 * 
 */
@BotCommand
public class HoroscopeCommand {

	@Inject
	State state;

	@Inject
	BotUtilsService utilsService;
	
	@Inject
	Logger log;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		Map<String, String> mapping = state.get(Map.class);
		if (mapping == null) {
			mapping = Maps.newLinkedHashMap();
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
			state.put(mapping);
		}
	}

	@Trigger(value = "!horoscope", type = TriggerType.EXACTMATCH)
	@Help("Prints help on how to use this command.")
	public String getHoroscopeHelp() {
		String availableSigns = StringUtils.join(state.get(Map.class).keySet(),
				", ");
		return IRCUtils.bold("!horoscope <signe>") + " - Available signs : "
				+ availableSigns;
	}

	@SuppressWarnings("unchecked")
	@Trigger(value = "!horoscope", type = TriggerType.STARTSWITH)
	public String getHoroscope(TriggerEvent event) {
		String sign = event.getMessage();
		if ("poisson".equals(sign)) {
			sign = "poissons";
		}

		String line = null;
		try {
			Map<String, String> mapping = state.get(Map.class);
			String id = mapping.get(sign);
			Document doc = Jsoup
					.parse(utilsService
							.getContent("http://www.astrocenter.fr/fr/FCDefault.aspx?Af=0&sign="
									+ id));

			Element horo = doc.select("div#ast-sign-" + id).first();
			horo = horo.select(".ast-description p").first();
			return horo.text();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return line;
	}

}