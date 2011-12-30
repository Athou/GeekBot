package be.hehehe.geekbot.commands;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;

@BotCommand
public class Rot13Command {

	@Trigger(value = "!rot13", type = TriggerType.STARTSWITH)
	@Help("\"Crypts\" the given sentence.")
	public String update(TriggerEvent event) {
		StringBuilder message = new StringBuilder();

		for (char c : event.getMessage().toCharArray()) {
			if (c >= 'a' && c <= 'm')
				c += 13;
			else if (c >= 'n' && c <= 'z')
				c -= 13;
			else if (c >= 'A' && c <= 'M')
				c += 13;
			else if (c >= 'N' && c <= 'Z')
				c -= 13;
			message.append(c);
		}

		return message.toString();
	}
}
