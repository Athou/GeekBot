package be.hehehe.geekbot.commands;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Maps;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.DiscordUtils;
import lombok.extern.jbosslog.JBossLog;

/**
 * Horoscope from astrocenter.fr (French)
 * 
 */
@BotCommand
@JBossLog
public class HoroscopeCommand {

	@Inject
	BotUtilsService utilsService;

	private final Map<String, String> mapping = buildMapping();

	private Map<String, String> buildMapping() {
		Map<String, String> mapping = Maps.newLinkedHashMap();
		mapping.put("belier", "belier");
		mapping.put("bélier", "belier");
		mapping.put("taureau", "taureau");
		mapping.put("gemeaux", "gemeaux");
		mapping.put("gémeaux", "gemeaux");
		mapping.put("cancer", "cancer");
		mapping.put("lion", "lion");
		mapping.put("vierge", "vierge");
		mapping.put("balance", "balance");
		mapping.put("scorpion", "scorpion");
		mapping.put("sagittaire", "sagittaire");
		mapping.put("capricorne", "capricorne");
		mapping.put("verseau", "verseau");
		mapping.put("poisson", "poissons");
		mapping.put("poissons", "poissons");
		return mapping;
	}

	@Trigger(value = "!horoscope", type = TriggerType.EXACTMATCH)
	@Help("Prints help on how to use this command.")

	public String getHoroscopeHelp() {
		String availableSigns = StringUtils.join(mapping.keySet(), ", ");
		return DiscordUtils.bold("!horoscope <signe>") + " - Available signs : " + availableSigns;
	}

	@Trigger(value = "!horoscope", type = TriggerType.STARTSWITH)
	public String getHoroscope(TriggerEvent event) {
		String sign = event.getMessage();

		String content = null;
		String line = null;
		try {
			String id = mapping.get(sign);
			if (id == null) {
				return null;
			}

			String url = String.format("https://www.mon-horoscope-du-jour.com/horoscopes/quotidien/%s.htm", id);
			content = utilsService.getContent(url, StandardCharsets.ISO_8859_1.name());
			Document doc = Jsoup.parse(content);

			Element title = doc.select("h2:contains(Notre conseil du jour)").first();
			Element horo = title.parent().child(2);
			line = horo.text();

		} catch (Exception e) {
			log.error("Could not parse HTML" + e.getMessage() + SystemUtils.LINE_SEPARATOR + content, e);
		}

		return line;
	}

}