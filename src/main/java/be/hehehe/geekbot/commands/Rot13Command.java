package be.hehehe.geekbot.commands;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class Rot13Command {

	@Trigger(value = "!rot13", type = TriggerType.STARTSWITH)
	public String update(TriggerEvent event) {
		String message = "";
		
		String text = event.getMessage();
		for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'A' && c <= 'Z') c -= 13;
            message += c;
        }
		
		return message;
	}
}
