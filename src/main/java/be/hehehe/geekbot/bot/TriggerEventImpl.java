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
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public Collection<String> getUsers() {
		return users;
	}

	public void setUsers(Collection<String> users) {
		this.users = users;
	}

	@Override
	public boolean isNickInMessage() {
		return nickInMessage;
	}

	public void setNickInMessage(boolean nickInMessage) {
		this.nickInMessage = nickInMessage;
	}

	@Override
	public boolean isBotInMessage() {
		return botInMessage;
	}

	public void setBotInMessage(boolean botInMessage) {
		this.botInMessage = botInMessage;
	}

	@Override
	public String getMessageWithoutTrigger() {
		return messageWithoutTrigger;
	}

	public void setMessageWithoutTrigger(String messageWithoutTrigger) {
		this.messageWithoutTrigger = messageWithoutTrigger;
	}

}
