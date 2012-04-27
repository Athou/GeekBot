package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Stores all lines spoken on the channel. The bot will also give one of those
 * sentences back when addressed or randomly in a conversation.
 * 
 * 
 */
@BotCommand
public class ConnerieCommand {

	@Inject
	ConnerieDAO dao;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> storeEveryLines(TriggerEvent event) {
		String message = event.getMessage();
		List<String> result = new ArrayList<String>();
		if (!event.hasURL()) {
			if (!event.isNickInMessage() && message.length() > 9
					&& !message.contains("<") && !message.contains(">")
					&& !event.isStartsWithTrigger()) {
				Connerie connerie = new Connerie(message);
				dao.save(connerie);
			}
		}

		return result;
	}

	@RandomAction(3)
	@Trigger(type = TriggerType.BOTNAME)
	public String getRandomLine() {
		return dao.getRandom().getValue();
	}

	@Trigger(value = "!rand", type = TriggerType.STARTSWITH)
	@Help("Returns a random sentence matching given arguments.")
	public String getRandQuote(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				Connerie connerie = dao.getRandomMatching(keywords.split(" "));
				if (connerie != null) {
					r = connerie.getValue();
				}
			}
		}
		return r;
	}

	@Trigger(value = "!stat", type = TriggerType.STARTSWITH)
	@Help("Count sentences containing the given arguments in our database.")
	public String getStatCount(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				int count = dao.getCountMatching(keywords.split("[  ]"));
				r = IRCUtils.bold("Stat count for \"" + keywords + "\" : ")
						+ count;
			}
		}
		return r;
	}

}
