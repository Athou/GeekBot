package be.hehehe.geekbot.commands;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.utils.IRCUtils;

@BotCommand
public class StatCommand {
	@Trigger(value = "!stat", type = TriggerType.STARTSWITH)
	public String getStatCount(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				ConnerieDAO dao = new ConnerieDAO();
				int count = dao.getCountMatching(keywords.split("[  ]"));
				r = IRCUtils.bold("Stat count for \"" + keywords + "\" : ")
						+ count;
			}
		}
		return r;
	}
}
