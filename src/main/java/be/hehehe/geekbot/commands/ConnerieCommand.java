package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.lucene.ConnerieIndexService;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.BotUtilsService;

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
					&& !message.startsWith("!")) {
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
		message = message.replace("?", "");
		List<String> list = connerieIndexService.findRelated(message,
				lastReadSentences, MAX_STORED_SENTENCES);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String line = it.next();
			if (lastSpokenSentences.contains(line)) {
				it.remove();
			}
		}

		Random random = new Random();
		int irand = random.nextInt(list.size());

		message = list.get(irand);
		pushSentence(message, lastSpokenSentences);
		return message;
	}

}
