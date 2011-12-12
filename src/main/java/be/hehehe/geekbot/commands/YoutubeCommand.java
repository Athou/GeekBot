package be.hehehe.geekbot.commands;

import java.util.List;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;

@BotCommand
public class YoutubeCommand {

	@Trigger(value = "!youtube", type = TriggerType.STARTSWITH)
	public List<String> getYoutubeResults(String keywords) {
		return new GoogleCommand().google("site:www.youtube.com " + keywords);
	}
}
