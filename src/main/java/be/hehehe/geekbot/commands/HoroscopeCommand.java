package be.hehehe.geekbot.commands;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
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
			mapping.put("belier", "bélier");
			mapping.put("taureau", "taureau");
			mapping.put("gemeaux", "gémeaux");
			mapping.put("cancer", "cancer");
			mapping.put("lion", "lion");
			mapping.put("vierge", "vierge");
			mapping.put("balance", "balance");
			mapping.put("scorpion", "scorpion");
			mapping.put("sagittaire", "sagittaire");
			mapping.put("capricorne", "capricorne");
			mapping.put("verseau", "verseau");
			mapping.put("poissons", "poissons");
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

		String content = null;
		String line = null;
		try {
			Map<String, String> mapping = state.get(Map.class);
			String id = mapping.get(sign);
			content = utilsService.getContent(String.format(
					"http://fr.astrology.yahoo.com/horoscope/%s/", id));
			Document doc = Jsoup.parse(content);

			Element horo = doc.select(".astro-tab-body").first();
			line = horo.text();

		} catch (Exception e) {
			log.error("Could not parse HTML" + e.getMessage()
					+ SystemUtils.LINE_SEPARATOR + content, e);
		}

		return line;
	}

}