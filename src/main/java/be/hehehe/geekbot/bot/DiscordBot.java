package be.hehehe.geekbot.bot;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import lombok.extern.jbosslog.JBossLog;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@JBossLog
public class DiscordBot extends ListenerAdapter {

	private final String botName;
	private final MessageListener listener;

	private JDA jda;
	private Guild guild;

	public DiscordBot(String token, String botName, MessageListener listener) {
		this.botName = botName;
		this.listener = listener;
		try {
			jda = JDABuilder.createDefault(token).addEventListeners(this).build().awaitReady();
			guild = jda.getGuilds().get(0);
		} catch (LoginException | InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String sender = event.getMember().getEffectiveName();
		if (!botName.equalsIgnoreCase(sender)) {
			listener.onMessage(event.getTextChannel().getName(), sender, event.getMessage().getContentDisplay());
		}
	}

	public interface MessageListener {
		void onMessage(String channel, String sender, String message);
	}

	public void sendMessage(String channel, String message) {
		guild.getTextChannelsByName(channel, true).get(0).sendMessage(message).queue();
	}

	public List<String> getUsers(String channel) {
		List<TextChannel> channels = guild.getTextChannelsByName(channel, true);
		if (channels.isEmpty()) {

			return Collections.emptyList();
		}

		return channels.get(0).getMembers().stream().map(m -> m.getNickname()).collect(Collectors.toList());
	}
}
