package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import com.google.common.collect.Lists;

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
		List<String> result = Lists.newArrayList();
		String key = bundleService.getVDMApiKey();
		if (StringUtils.isBlank(key)) {
			result.add("VDM API key not set.");
			return result;
		}

		String xml = null;
		try {
			String url = "http://api.betacie.com/view/random/nocomment/?key="
					+ key + "&language=fr";
			xml = utilsService.getContent(url);
			Document doc = utilsService.parseXML(xml);

			Element root = doc.getRootElement();
			String text = root.getChild("items").getChild("item")
					.getChild("text").getValue();
			text = text.replaceAll("(\\r|\\n)", "");

			result.add(IRCUtils.bold("VDM") + " - " + text);
			result.add(IRCUtils.bold("MOAR FAKE PLZ"));

		} catch (Exception e) {
			result.add("Could not fetch a VDM for some reason.");
			log.error(e.getMessage(), e);
		}
		return result;

	}
}
