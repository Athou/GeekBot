package be.hehehe.geekbot.commands;

import java.util.List;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.commands.GoogleCommand.Lang;
import be.hehehe.geekbot.commands.GoogleCommand.Mode;

@BotCommand
public class YoutubeCommand {

	@Trigger(value = "!youtube", type = TriggerType.STARTSWITH)
	public List<String> getYoutubeResults(TriggerEvent event) {
		return GoogleCommand.google(
				"site:www.youtube.com " + event.getMessageWithoutTrigger(),
				Lang.ENGLISH, Mode.WEB);
	}
}
