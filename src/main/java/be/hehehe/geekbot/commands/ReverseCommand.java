package be.hehehe.geekbot.commands;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;

/**
 * Reverse the given string.
 * 
 */
@BotCommand
public class ReverseCommand {

	@Trigger(value = "!reverse", type = TriggerType.STARTSWITH)
	@Help("Reverse the given string.")
	public String reverse(TriggerEvent e) {
		String message = e.getMessage();
		return StringUtils.reverse(message);
	}

}
