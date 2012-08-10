package be.hehehe.geekbot.commands;

import java.util.Random;

import javax.inject.Inject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;

/**
 * Prints either Pile or Face
 * 
 */
@BotCommand
public class PileoufaceCommand {

	@Inject
	Random random;

	@Trigger(value = "!pileouface")
	@Help("Prints either Pile or Face.")
	public String pileouface() {
		return random.nextBoolean() ? "Pile" : "Face";
	}
}
