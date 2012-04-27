package be.hehehe.geekbot.commands;

import java.util.Random;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;

/**
 * Prints either Pile or Face
 * 
 */
@BotCommand
public class PileoufaceCommand {

	@Trigger(value = "!pileouface")
	@Help("Prints either Pile or Face.")
	public String pileouface() {
		return new Random().nextBoolean() ? "Pile" : "Face";
	}
}
