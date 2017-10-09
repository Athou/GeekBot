package be.hehehe.geekbot.commands;

import javax.inject.Inject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.BotUtilsService;

/**
 * Mirrors images on imgur.com, useful for image hosts blocked at work. When the trigger is invoked without arguments, the last url on the
 * channel will be mirrored.
 * 
 */
@BotCommand
public class MirrorCommand {

	@Inject
	State state;

	@Inject
	BotUtilsService utilsService;

	@Trigger("!mirror")
	@Help("Mirrors the last link pasted on the chan.")
	public String getMirrorImage(TriggerEvent event) {
		String result = null;
		String lastUrl = state.get(String.class);
		if (lastUrl != null) {
			result = handleURL(lastUrl, event);
		}
		return result;
	}

	@Trigger(value = "!mirror", type = TriggerType.STARTSWITH)
	@Help("Mirrors the given url.")
	public String getMirrorImage2(TriggerEvent event) {
		String result = handleURL(event.getURL(), event);
		return result;
	}

	@Trigger(type = TriggerType.EVERYTHING)
	public void storeLastURL(TriggerEvent event) {
		if (event.hasURL()) {
			state.put(event.getURL());
		}
		return;
	}

	private String handleURL(String message, TriggerEvent event) {
		String result = null;
		if (message.endsWith(".png") || message.endsWith(".jpg") || message.endsWith(".gif") || message.endsWith(".bmp")) {
			try {
				result = utilsService.mirrorImage(message);
			} catch (Exception e) {
				result = "Error retrieving file";
			}
		}
		return result;
	}

}
