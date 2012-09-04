package be.hehehe.geekbot.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.SkanditeDAO;
import be.hehehe.geekbot.persistence.model.Skandite;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.HashAndByteCount;
import be.hehehe.geekbot.utils.IRCUtils;

/**
 * Stores all links and gives an alert when a link has already been posted on
 * the chan.
 * 
 */
@BotCommand
public class SkanditeCommand {

	@Inject
	BotUtilsService utilsService;

	@Inject
	SkanditeDAO dao;

	@Trigger(type = TriggerType.EVERYTHING)
	public List<String> handleSkandites(TriggerEvent event) {

		List<String> result = new ArrayList<String>();
		if (event.hasURL()) {
			String url = event.getURL();
			Skandite skandite = dao.findByURL(url);
			HashAndByteCount hashAndByteCount = null;
			if (skandite == null) {
				hashAndByteCount = utilsService.calculateHashAndByteCount(url);
				if (hashAndByteCount != null) {
					skandite = dao.findByHashAndByteCount(
							hashAndByteCount.getHash(),
							hashAndByteCount.getByteCount());
				}
			}

			if (skandite != null) {
				if (!StringUtils
						.equals(skandite.getAuthor(), event.getAuthor())) {
					String line = IRCUtils.bold("Skandite! ")
							+ skandite.getUrl()
							+ " linked "
							+ utilsService.getTimeDifference(skandite
									.getPostedDate()) + " ago by "
							+ skandite.getAuthor() + " (" + skandite.getCount()
							+ "x).";
					result.add(line);
					skandite.setCount(skandite.getCount() + 1);
					dao.update(skandite);
				}
			} else {
				skandite = new Skandite();
				skandite.setUrl(url);
				skandite.setPostedDate(new Date());
				skandite.setAuthor(event.getAuthor());
				skandite.setCount(1l);
				if (hashAndByteCount != null) {
					skandite.setHash(hashAndByteCount.getHash());
					skandite.setByteCount(hashAndByteCount.getByteCount());
				}
				dao.save(skandite);
			}

		}
		return result;
	}
}
