package be.hehehe.geekbot.annotations;

/**
 * 
 * Trigger types.
 * 
 */
public enum TriggerType {
	/**
	 * Triggers if the whole message matches the trigger.
	 */
	EXACTMATCH,
	/**
	 * Triggers if the message starts with the trigger.
	 */
	STARTSWITH,
	/**
	 * Triggers if the message contains the nick name of the bot
	 */
	BOTNAME,
	/**
	 * Triggers on everything
	 */
	EVERYTHING;

}
