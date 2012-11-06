package be.hehehe.geekbot.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.BotUtilsService;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

@BotCommand
public class MemeCommand {

	private static final String CREATE_URL = "http://version1.api.memegenerator.net/Instance_Create?username=%s&password=%s&languageCode=en&generatorID=%s&imageID=%s&text0=%s&text1=%s";
	private static final String POPULAR_GENERATORS = "http://version1.api.memegenerator.net/Generators_Select_ByPopular?pageIndex=%d&pageSize=24&days=3";

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
		// one does not simply
		generators.add(new Generator("274947", "1865027"));
		// nailed it
		generators.add(new Generator("121", "1031"));
		// too damn
		generators.add(new Generator("998", "203665"));

		for (int i = 0; i < 6; i++) {
			addPopular(i);
		}
	}

	private void addPopular(int index) {
		try {
			String content = utilsService.getContent(String.format(
					POPULAR_GENERATORS, index));
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

	@TimedAction(value = 24, timeUnit = TimeUnit.HOURS)
	private void refreshGenerators() {
		generators.clear();
		init();
	}

	@Trigger(type = TriggerType.STARTSWITH, value = "!meme")
	public String generateWithArgument(TriggerEvent event) {
		String result = null;

		String message = event.getMessage();
		List<String> tokens = getTokens(message);

		if (StringUtils.isBlank(message)) {
			result = generateWithoutArgument();
		} else if (tokens.size() == 1) {
			result = generate(tokens.get(0), null);
		} else if (tokens.size() > 1) {
			result = generate(tokens.get(0), tokens.get(1));
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
			instanceImageUrl = instanceImageUrl.replace("400x", "800x");
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
			Connerie connerie = dao.getRandomMatching(like.split(" "));
			if (connerie != null) {
				text = connerie.getValue();
			}
		}

		if (text == null) {
			text = dao.getRandom().getValue();
		}
		text = utilsService.utf8ToIso88591(text);
		text = utilsService.stripAccents(text);
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// do nothing
		}
		return text;
	}

	private List<String> getTokens(String message) {
		List<String> list = Lists.newArrayList();
		String regex = "\"([^\"]*)\"|(\\S+)";

		Matcher m = Pattern.compile(regex).matcher(message);
		while (m.find()) {
			if (m.group(1) != null) {
				list.add(m.group(1));
			} else {
				list.add(m.group(2));
			}
		}
		return list;
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
