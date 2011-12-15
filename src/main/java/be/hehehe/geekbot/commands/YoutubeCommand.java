package be.hehehe.geekbot.commands;

import java.util.List;

import javax.inject.Inject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.commands.GoogleCommand.Lang;
import be.hehehe.geekbot.commands.GoogleCommand.Mode;

@BotCommand
public class YoutubeCommand {

	@Inject
	GoogleCommand googleCommand;

	@Trigger(value = "!youtube", type = TriggerType.STARTSWITH)
	public List<String> getYoutubeResults(TriggerEvent event) {
		return googleCommand.google(
				"site:www.youtube.com " + event.getMessage(), Lang.ENGLISH,
				Mode.WEB);
	}
}
