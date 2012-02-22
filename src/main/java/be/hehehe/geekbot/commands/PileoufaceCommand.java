package be.hehehe.geekbot.commands;

import java.util.Random;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;

@BotCommand
public class PileoufaceCommand {

	@Trigger(value = "!pileouface")
	public String pileouface() {
		return new Random().nextBoolean() ? "Pile" : "Face";
	}
}
