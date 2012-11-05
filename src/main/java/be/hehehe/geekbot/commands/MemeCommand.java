package be.hehehe.geekbot.commands;

import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;

import com.google.common.collect.Lists;

@BotCommand
public class MemeCommand {

	private static final String URL = "http://version1.api.memegenerator.net/Instance_Create?username=%s&password=%s&languageCode=en&generatorID=%d&imageID=%d&text0=%s&text1=%s";

	@Inject
	Logger log;

	@Inject
	ConnerieDAO dao;

	@Inject
	Random random;

	@Inject
	BundleService bundle;

	@Inject
	BotUtilsService utilsService;

	private List<Phrase> phrases = Lists.newArrayList();

	@PostConstruct
	public void init() {
		phrases.add(new Phrase("%s", "Y U NO %s", true, true, 2, 166088));
		phrases.add(new Phrase("I DON'T ALWAYS %s", "BUT WHEN I DO, %s", true,
				true, 74, 2485));
		phrases.add(new Phrase("%s", "O RLY?", true, false, 920, 117049));
		phrases.add(new Phrase("%s", "ALL THE %s", true, true, 6013, 1121885));
		phrases.add(new Phrase("GOOD NEWS EVERYONE", "%s", false, true, 1591,
				112464));
		phrases.add(new Phrase("NOT SURE IF %s", "OR %s", true, true, 305,
				84688));
		phrases.add(new Phrase("YO DAWG %s", "SO %s", true, true, 79, 108785));
		phrases.add(new Phrase("ALL YOUR %s", "ARE BELONG TO US", true, false,
				349058, 2079825));
		phrases.add(new Phrase("%s", "FUCK YOU", true, false, 1189472, 5044147));
		phrases.add(new Phrase("%s", "You're gonna have a bad time", true,
				false, 825296, 3786537));
		phrases.add(new Phrase("one does not simply", "%s", false, true,
				274947, 1865027));
		phrases.add(new Phrase("%s", "NAILED IT", true, false, 121, 1031));
		phrases.add(new Phrase("%s", "TOO DAMN %s", true, true, 998, 203665));
	}

	@Trigger("!meme")
	public String generate() {
		String result = null;

		try {
			int index = random.nextInt(phrases.size());
			Phrase phrase = phrases.get(index);

			String text0 = phrase.getText0();
			if (phrase.isReplace0()) {
				text0 = String.format(text0, dao.getRandom().getValue());
			}
			text0 = URLEncoder.encode(text0, "UTF-8");

			String text1 = phrase.getText1();
			if (phrase.isReplace1()) {
				text1 = String.format(text1, dao.getRandom().getValue());
			}
			text1 = URLEncoder.encode(text1, "UTF-8");

			int generatorId = phrase.getGeneratorId();
			int imageId = phrase.getImageId();

			String login = bundle.getMemeGeneratorLogin();
			String password = bundle.getMemeGeneratorPassword();

			String url = String.format(URL, login, password, generatorId,
					imageId, text0, text1);

			String content = utilsService.getContent(url);

			JSONObject json = new JSONObject(content);
			JSONObject resultObject = json.getJSONObject("result");
			String instanceImageUrl = resultObject
					.getString("instanceImageUrl");
			result = instanceImageUrl;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = e.getMessage();
		}
		return result;
	}

	private class Phrase {
		private String text0;
		private String text1;
		private boolean replace0;
		private boolean replace1;
		private int generatorId;
		private int imageId;

		public Phrase(String text0, String text1, boolean replace0,
				boolean replace1, int generatorId, int imageId) {
			this.text0 = text0;
			this.text1 = text1;

			this.replace0 = replace0;
			this.replace1 = replace1;

			this.generatorId = generatorId;
			this.imageId = imageId;
		}

		public String getText0() {
			return text0;
		}

		public String getText1() {
			return text1;
		}

		public boolean isReplace0() {
			return replace0;
		}

		public boolean isReplace1() {
			return replace1;
		}

		public int getGeneratorId() {
			return generatorId;
		}

		public int getImageId() {
			return imageId;
		}

	}
}
