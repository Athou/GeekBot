package be.hehehe.geekbot.commands;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BundleUtil;
import be.hehehe.geekbot.utils.IRCUtils;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class VDMCommand {

	@Trigger("!vdm")
	public List<String> getRandomVDM() {
		List<String> toReturn = new ArrayList<String>();
		String key = BundleUtil.getVDMApiKey();
		if (StringUtils.isBlank(key)) {
			toReturn.add("VDM API key not set.");
			return toReturn;
		}
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new URL(
					"http://api.betacie.com/view/random/nocomment/?key=" + key
							+ "&language=fr"));
			Element root = doc.getRootElement();
			String text = root.getChild("items").getChild("item")
					.getChild("text").getValue();
			text = text.replaceAll("(\\r|\\n)", "");

			toReturn.add(IRCUtils.bold("VDM") + " - " + text);
			toReturn.add(IRCUtils.bold("MOAR FAKE PLZ"));

		} catch (Exception e) {
			LOG.handle(e);
		}
		return toReturn;

	}
}
