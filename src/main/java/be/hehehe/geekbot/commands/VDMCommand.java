package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;

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
		List<String> toReturn = new ArrayList<String>();
		String key = bundleService.getVDMApiKey();
		if (StringUtils.isBlank(key)) {
			toReturn.add("VDM API key not set.");
			return toReturn;
		}
		try {
			String url = "http://api.betacie.com/view/random/nocomment/?key="
					+ key + "&language=fr";
			Document doc = utilsService.parseXML(utilsService.getContent(url));

			Element root = doc.getRootElement();
			String text = root.getChild("items").getChild("item")
					.getChild("text").getValue();
			text = text.replaceAll("(\\r|\\n)", "");

			toReturn.add(IRCUtils.bold("VDM") + " - " + text);
			toReturn.add(IRCUtils.bold("MOAR FAKE PLZ"));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return toReturn;

	}
}
