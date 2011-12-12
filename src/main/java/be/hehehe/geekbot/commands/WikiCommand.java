package be.hehehe.geekbot.commands;

import java.util.List;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;

@BotCommand
public class WikiCommand {

	@Trigger(value = "!wiki", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults(String keywords) {
		return new GoogleCommand().google("wikipedia " + keywords);
	}
	
	@Trigger(value = "!wikipedia", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults2(String keywords) {
		return new GoogleCommand().google("wikipedia " + keywords);
	}
	
	@Trigger(value = "!wikifr", type = TriggerType.STARTSWITH)
	public List<String> getWikiResultsfr(String keywords) {
		return new GoogleCommand().googlefr("wikipedia " + keywords);
	}
	
	@Trigger(value = "!wikipediafr", type = TriggerType.STARTSWITH)
	public List<String> getWikiResults2fr(String keywords) {
		return new GoogleCommand().googlefr("wikipedia " + keywords);
	}
}
