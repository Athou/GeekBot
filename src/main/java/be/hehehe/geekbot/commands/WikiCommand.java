package be.hehehe.geekbot.commands;

import java.util.List;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.commands.GoogleCommand.Lang;
import be.hehehe.geekbot.commands.GoogleCommand.Mode;

@BotCommand
public class WikiCommand {

	@Trigger(value = "!wiki", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults(TriggerEvent event) {
		return GoogleCommand.google(
				"wikipedia " + event.getMessageWithoutTrigger(), Lang.ENGLISH,
				Mode.WEB);
	}

	@Trigger(value = "!wikipedia", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults2(TriggerEvent event) {
		return getWikiResults(event);
	}

	@Trigger(value = "!wikifr", type = TriggerType.STARTSWITH)
	public List<String> getWikiResultsfr(TriggerEvent event) {
		return GoogleCommand.google(
				"wikipedia " + event.getMessageWithoutTrigger(), Lang.FRENCH,
				Mode.WEB);
	}

	@Trigger(value = "!wikipediafr", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults2fr(TriggerEvent event) {
		return getWikiResultsfr(event);
	}
}
