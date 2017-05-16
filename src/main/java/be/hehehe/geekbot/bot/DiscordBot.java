package be.hehehe.geekbot.bot;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

	@Inject
	Logger log;

	private MessageListener listener;

	private JDA jda;
	private Guild guild;

	public DiscordBot(String token, MessageListener listener) {
		this.listener = listener;
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(this).buildBlocking();
			guild = jda.getGuilds().get(0);
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		listener.onMessage(event.getTextChannel().getName(), event.getMember().getEffectiveName(), event.getMessage().getContent());
	}

	public static interface MessageListener {
		public void onMessage(String channel, String sender, String message);
	}

	public void sendMessage(String channel, String message) {
		guild.getTextChannelsByName(channel, true).get(0).sendMessage(message);
	}

	public List<String> getUsers(String channel) {
		return guild.getTextChannelsByName(channel, true).get(0).getMembers().stream().map(m -> m.getNickname())
				.collect(Collectors.toList());
	}
}
