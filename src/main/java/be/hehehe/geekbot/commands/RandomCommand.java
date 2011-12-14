package be.hehehe.geekbot.commands;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;

@BotCommand
public class RandomCommand {
	@Trigger(value = "!rand", type = TriggerType.STARTSWITH)
	public String getRandQuote(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				ConnerieDAO dao = new ConnerieDAO();
				Connerie connerie = dao.getRandomMatching(keywords
						.split("[  ]"));
				if (connerie != null) {
					r = connerie.getValue();
				}
			}
		}
		return r;
	}
}
