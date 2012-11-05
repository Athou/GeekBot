package be.hehehe.geekbot.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

@BotCommand
public class MemeCommand {

	private static final String CREATE_URL = "http://version1.api.memegenerator.net/Instance_Create?username=%s&password=%s&languageCode=en&generatorID=%s&imageID=%s&text0=%s&text1=%s";
	private static final String POPULAR_GENERATORS = "http://version1.api.memegenerator.net/Generators_Select_ByPopular?pageIndex=0&pageSize=24&days=3";

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

	private List<Generator> generators = Lists.newCopyOnWriteArrayList();

	@PostConstruct
	private void init() {
		// y u no
		generators.add(new Generator("2", "166088"));
		// most interesting man on earth
		generators.add(new Generator("74", "2485"));
		// o'rly
		generators.add(new Generator("920", "117049"));
		// * all the things
		generators.add(new Generator("6013", "1121885"));
		// good news everyone
		generators.add(new Generator("1591", "112464"));
		// fry not sure if
		generators.add(new Generator("305", "84688"));
		// yo dawg
		generators.add(new Generator("79", "108785"));
		// all your base are belong to us
		generators.add(new Generator("349058", "2079825"));
		// fuck you
		generators.add(new Generator("1189472", "5044147"));
		// You're gonna have a bad time
		generators.add(new Generator("825296", "3786537"));
		// one does not simply
		generators.add(new Generator("274947", "1865027"));
		// nailed it
		generators.add(new Generator("121", "1031"));
		// to damn
		generators.add(new Generator("998", "203665"));

		addPopular();
	}

	private void addPopular() {
		try {
			String content = utilsService.getContent(POPULAR_GENERATORS);
			JSONObject json = new JSONObject(content);
			JSONArray results = json.getJSONArray("result");
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
				String generatorId = result.getString("generatorID");

				String imageUrl = result.getString("imageUrl");
				String[] tokens = imageUrl.split(Pattern.quote("/"));
				String imageName = tokens[tokens.length - 1];
				String imageId = imageName.split(Pattern.quote("."))[0];

				generators.add(new Generator(generatorId, imageId));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@TimedAction(value = 6, timeUnit = TimeUnit.HOURS)
	private void refreshGenerators() {
		generators.clear();
		init();
	}

	@Trigger(type = TriggerType.STARTSWITH, value = "!meme")
	public String generateWithArgument(TriggerEvent event) {
		String result = null;

		String message = event.getMessage();
		String[] tokens = message.split(" ");

		if (tokens.length > 2) {
			result = generate(tokens[0], tokens[1]);
		} else {
			result = generateWithoutArgument();
		}
		return result;
	}

	@Trigger("!meme")
	public String generateWithoutArgument() {
		return generate(null, null);
	}

	public String generate(String term1, String term2) {
		String result = null;

		try {
			int index = random.nextInt(generators.size());
			Generator generator = generators.get(index);

			String text0 = getRandomText(term1);
			String text1 = getRandomText(term2);

			String generatorId = generator.getGeneratorId();
			String imageId = generator.getImageId();

			String login = bundle.getMemeGeneratorLogin();
			String password = bundle.getMemeGeneratorPassword();

			String url = String.format(CREATE_URL, login, password,
					generatorId, imageId, text0, text1);

			String content = utilsService.getContent(url);

			JSONObject json = new JSONObject(content);
			JSONObject resultObject = json.getJSONObject("result");
			String instanceImageUrl = resultObject
					.getString("instanceImageUrl");
			instanceImageUrl.replace("400x", "800x");
			result = instanceImageUrl;
			String imgur = utilsService.mirrorImage(result);
			if (imgur != null) {
				result = imgur;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = e.getMessage();
		}
		return IRCUtils.bold("10blague! ") + result;
	}

	private String getRandomText(String like) {
		String text = null;

		if (like != null) {
			text = dao.getRandomMatching(like.split(" ")).getValue();
		}

		if (text == null) {
			text = dao.getRandom().getValue();
		}
		text = utilsService.stripAccents(text);
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// do nothing
		}
		return text;
	}

	private class Generator {
		private String generatorId;
		private String imageId;

		public Generator(String generatorId, String imageId) {
			this.generatorId = generatorId;
			this.imageId = imageId;
		}

		public String getGeneratorId() {
			return generatorId;
		}

		public String getImageId() {
			return imageId;
		}

	}
}
