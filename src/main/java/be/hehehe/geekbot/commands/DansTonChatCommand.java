package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

@BotCommand
public class DansTonChatCommand {

	// key from android apk
	private static final String API_URL = "http://api.danstonchat.com/0.2/view/random0?key=cad048f021e9413b";

	@Inject
	BotUtilsService utilsService;

	@Inject
	Logger log;

	@Trigger("!dtc")
	public List<String> getRandom() {
		List<String> list = Lists.newArrayList();
		try {
			String xml = utilsService.getContent(API_URL);
			Document doc = utilsService.parseXML(xml);
			Element item = null;
			for (Element element : doc.getRootElement().getChildren("item")) {
				String content = element.getChildText("content");
				if (content.split(SystemUtils.LINE_SEPARATOR).length <= 5) {
					item = element;
					break;
				}
			}
			
			if(item == null) {
				throw new RuntimeException("No item found.");
			}
			
			String content = item.getChildText("content");
			String upvote = item.getChildText("vote_plus");
			String downvote = item.getChildText("vote_minus");
			for (String line : content.split(SystemUtils.LINE_SEPARATOR)) {
				list.add(line);
			}
			list.add(IRCUtils.bold("LOL je sé pa ékrir MDR")
					+ String.format(" (+%s/-%s)", upvote, downvote));
		} catch (Exception e) {
			list.add("Could not contact DTC.");
			log.error(e.getMessage(), e);
		}
		return list;
	}

}
