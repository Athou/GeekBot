package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.lucene.ConnerieIndexService;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Stores all lines spoken on the channel. The bot will also give one of those
 * sentences back when addressed or randomly in a conversation. The bot tries to
 * find a sentence that match the context of the current conversation.
 * 
 * 
 */
@BotCommand
public class ConnerieCommand {

	private static final List<String> lastReadSentences = new ArrayList<String>();
	private static final List<String> lastSpokenSentences = new ArrayList<String>();

	private static final int MAX_STORED_SENTENCES = 5;

	@Inject
	BotUtilsService utilsService;

	@Inject
	ConnerieDAO dao;

	@Inject
	ConnerieIndexService connerieIndexService;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> storeEveryLines(TriggerEvent event) {
		String message = event.getMessage();
		List<String> result = new ArrayList<String>();
		String url = utilsService.extractURL(message);
		if (url == null) {
			pushSentence(message, lastReadSentences);
			if (!event.isNickInMessage() && message.length() > 9
					&& !message.contains("<") && !message.contains(">")
					&& !event.isStartsWithTrigger()) {
				Connerie connerie = new Connerie(message);
				dao.save(connerie);
			}
		}

		return result;
	}

	private void pushSentence(String message, List<String> sentences) {
		sentences.add(0, message);
		while (sentences.size() > MAX_STORED_SENTENCES) {
			sentences.remove(sentences.size() - 1);
		}
	}

	@RandomAction(3)
	@Trigger(type = TriggerType.BOTNAME)
	public String getRandomLine(TriggerEvent event) {
		String message = event.getMessage();
		List<String> list = connerieIndexService.findRelated(message,
				lastReadSentences, MAX_STORED_SENTENCES);

		Random random = new Random();
		int rand = random.nextInt(list.size());

		message = list.get(rand);
		pushSentence(message, lastSpokenSentences);
		return message;
	}

	@Trigger(value = "!rand", type = TriggerType.STARTSWITH)
	public String getRandQuote(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				Connerie connerie = dao.getRandomMatching(keywords.split(" "));
				if (connerie != null) {
					r = connerie.getValue();
				}
			}
		}
		return r;
	}

	@Trigger(value = "!stat", type = TriggerType.STARTSWITH)
	public String getStatCount(TriggerEvent event) {
		String r = null;
		String keywords = event.getMessage();
		if (StringUtils.isNotBlank(keywords)) {
			keywords = keywords.trim();
			if (keywords.length() > 1) {
				int count = dao.getCountMatching(keywords.split("[  ]"));
				r = IRCUtils.bold("Stat count for \"" + keywords + "\" : ")
						+ count;
			}
		}
		return r;
	}

}
