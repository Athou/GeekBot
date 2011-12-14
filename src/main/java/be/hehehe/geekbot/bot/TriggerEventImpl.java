package be.hehehe.geekbot.bot;

import java.util.Collection;

public class TriggerEventImpl implements TriggerEvent {
	private String message;
	private String author;
	private String messageWithoutTrigger;
	private Collection<String> users;
	private boolean nickInMessage;
	private boolean botInMessage;

	public TriggerEventImpl(String message, String author, String trigger,
			Collection<String> users, boolean nickInMessage,
			boolean botInMessage) {
		this.message = message;
		this.author = author;
		this.users = users;
		this.nickInMessage = nickInMessage;
		this.botInMessage = botInMessage;

		if (message != null && trigger != null) {
			this.messageWithoutTrigger = message.replace(trigger, "");
		} else {
			this.messageWithoutTrigger = message;
		}

	}

	@Override
	public String getOriginalMessage() {
		return message;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public Collection<String> getUsers() {
		return users;
	}

	@Override
	public boolean isNickInMessage() {
		return nickInMessage;
	}

	@Override
	public boolean isBotInMessage() {
		return botInMessage;
	}

	@Override
	public String getMessage() {
		return messageWithoutTrigger;
	}

}
