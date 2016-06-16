package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

/**
 * Gives back a random VDM (French)
 * 
 */
@BotCommand
public class VDMCommand {

	@Inject
	BundleService bundleService;

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger("!vdm")
	@Help("Prints a random VDM.")
	public List<String> getRandomVDM() {
		return parseVDM("random");

	}

	@Trigger(value = "!vdm", type = TriggerType.STARTSWITH)
	@Help("Prints given VDM id.")
	public List<String> getRandomVDM(TriggerEvent event) {
		return parseVDM(event.getMessage());

	}

	private List<String> parseVDM(String vdmId) {
		List<String> result = Lists.newArrayList();
		String key = bundleService.getVDMApiKey();
		if (StringUtils.isBlank(key)) {
			result.add("VDM API key not set.");
			return result;
		}

		try {
			String url = "http://api.betacie.com/view/" + vdmId + "/nocomment/?key=" + key + "&language=fr";
			String xml = utilsService.getContent(url);
			Document doc = utilsService.parseXML(xml);

			Element root = doc.getRootElement();
			Element item = root.getChild("items").getChild("item");
			String text = item.getChild("text").getValue();
			text = text.replaceAll("(\\r|\\n)", "");
			String upvote = item.getChild("agree").getValue();
			String downvote = item.getChild("deserved").getValue();

			result.add(IRCUtils.bold("VDM") + " - " + text);
			result.add(IRCUtils.bold("MOAR FAKE PLZ") + String.format(" (+%s/-%s)", upvote, downvote));

		} catch (Exception e) {
			result.add("Could not fetch a VDM for some reason.");
			log.error(e.getMessage(), e);
		}
		return result;
	}
}
