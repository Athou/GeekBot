package be.hehehe.geekbot.bot;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

public interface TriggerEvent {

	/**
	 * Returns the message that triggered this command, unaltered.
	 * 
	 * @return the original message, null for methods annotated with TimedAction
	 */
	String getOriginalMessage();

	/**
	 * Returns the message that triggered this command, without the trigger.
	 * 
	 * @return the the message without the trigger, null for methods annotated with TimedAction
	 */
	String getMessage();

	/**
	 * Returns the name of the author of the message;
	 * 
	 * @return the nick name of the author, null for methods annotated with TimedAction
	 */
	String getAuthor();

	/**
	 * Returns a list of users on the channel
	 * 
	 * @return a list of users
	 */
	Collection<String> getUsers();

	/**
	 * Returns true if the message contains an url.
	 * 
	 * @return true if an url was found in the message.
	 */
	boolean hasURL();

	/**
	 * Returns the first url found in the message.
	 * 
	 * @return the first url found in the message.
	 */
	String getURL();

	/**
	 * Returns true if the message contains a nickname from one of the users on the chan
	 * 
	 * @return true if someone was highlighted, false if not or for methods annotated with TimedAction
	 */
	boolean isNickInMessage();

	/**
	 * Returns true if the name of the bot is in the message
	 * 
	 * @return true if the bot was highlighted, false if not or for methods annotated with TimedAction
	 */
	boolean isBotInMessage();

	/**
	 * Returns true if the message starts with an existing trigger. Useful for commands with TriggerType.EVERYTHING
	 * 
	 * @return true if the message starts with an existing trigger registered in the bot.
	 */
	boolean isStartsWithTrigger();

	/**
	 * Writes something directly on the channel. Usefull to notify progress.
	 * 
	 * @param message
	 *            the message sent to the channel
	 */
	void write(String message);

	/**
	 * Get the common scheduler pool instead of creating yours.
	 * 
	 * @return the common scheduler pool
	 */
	ScheduledExecutorService getScheduler();

}