package be.hehehe.geekbot.commands;

import java.io.IOException;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.LOG;

@BotCommand
public class UpdateCommand {
	@Trigger(value = "!update")
	public String update() {
		String message = "";
		try {
			Runtime.getRuntime().exec("sh update.sh");
		} catch (IOException e) {
			LOG.handle(e);
			message = "Could not update !";
		}
		return message;
	}
}
