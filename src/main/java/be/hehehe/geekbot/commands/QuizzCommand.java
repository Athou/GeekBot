package be.hehehe.geekbot.commands;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.ServletMethod;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.ServletEvent;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

@BotCommand
public class QuizzCommand {

	@Inject
	State state;

	@Inject
	Logger log;

	@Inject
	BundleService bundleService;

	@Inject
	QuizzDAO dao;

	private static final String ENABLED = "enabled";
	private static final String QUESTIONS = "questions";
	private static final String CURRENT_ANSWER = "current-answer";
	private static final String TIMEOUT_TIMER = "timeout-timer";
	private static final String INDICE_TIMER = "indice-timer";
	private static final String NEXTQUESTION_TIMER = "nextquestion-timer";

	private static final String[] STOPWORDS = new String[] { "un", "une",
			"des", "le", "la", "les" };

	@Trigger(value = "!quizz")
	@Help("Starts the quizz.")
	public void startQuizz(TriggerEvent event) {
		if (Boolean.TRUE.equals(state.get(ENABLED))) {
			event.write("The quizz has already started.");
		} else {
			state.put(ENABLED, Boolean.TRUE);
			nextQuestion(event, 0);
		}
	}

	@Trigger(value = "!stop")
	@Help("Stops the quizz.")
	public void stopQuizz(TriggerEvent event) {
		stopIndiceTimer();
		stopNextQuestionTimer();
		stopTimeoutTimer();
		if (Boolean.TRUE.equals(state.get(ENABLED))) {
			event.write("Quizz stopped.");
		} else {
			event.write("Quizz is not running.");
		}
		state.put(ENABLED, Boolean.FALSE);
	}

	@Trigger(value = "!tg")
	@Help("Stops the quizz.")
	public void stopQuizz2(TriggerEvent event) {
		stopQuizz(event);
	}

	private void nextQuestion(final TriggerEvent event, int delay) {
		state.put(CURRENT_ANSWER, null);
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					List<String> lines = getQuestions();
					int rand = new Random().nextInt(lines.size());

					String line = lines.get(rand);
					String[] split = line.split("\\\\");

					String question = split[0].trim();
					String answer = split[1].trim();

					state.put(CURRENT_ANSWER, answer);

					event.write(IRCUtils.bold("Question! ") + question);

					startIndiceTimer(event);
					startTimeoutTimer(event);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		};
		ScheduledFuture<?> timer = event.getScheduler().schedule(thread, delay,
				TimeUnit.SECONDS);
		state.put(NEXTQUESTION_TIMER, timer);

	}

	private void stopNextQuestionTimer() {
		ScheduledFuture<?> timer = state.get(NEXTQUESTION_TIMER,
				ScheduledFuture.class);
		if (timer != null) {
			timer.cancel(true);
		}
	}

	private void startIndiceTimer(final TriggerEvent event) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				event.write(IRCUtils.bold("1 10: "
						+ computeIndice(state.get(CURRENT_ANSWER, String.class))));
			}

		};
		ScheduledFuture<?> timer = event.getScheduler().schedule(thread, 20,
				TimeUnit.SECONDS);
		state.put(INDICE_TIMER, timer);
	}

	private void stopIndiceTimer() {
		ScheduledFuture<?> timer = state.get(INDICE_TIMER,
				ScheduledFuture.class);
		if (timer != null) {
			timer.cancel(true);
		}
	}

	private void startTimeoutTimer(final TriggerEvent event) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				event.write(IRCUtils.bold("Trop tard! ") + "La réponse était: "
						+ state.get(CURRENT_ANSWER)
						+ ". Prochaine question dans quelques secondes ...");
				nextQuestion(event, 10);
			}
		};
		ScheduledFuture<?> timer = event.getScheduler().schedule(thread, 30,
				TimeUnit.SECONDS);
		state.put(TIMEOUT_TIMER, timer);
	}

	private void stopTimeoutTimer() {
		ScheduledFuture<?> timer = state.get(TIMEOUT_TIMER,
				ScheduledFuture.class);
		if (timer != null) {
			timer.cancel(true);
		}
	}

	@Trigger(type = TriggerType.EVERYTHING)
	public void handlePossibleAnswer(TriggerEvent event) {
		String answer = state.get(CURRENT_ANSWER, String.class);
		if (answer != null && Boolean.TRUE.equals(state.get(ENABLED))
				&& !event.isStartsWithTrigger()) {
			if (matches(event.getMessage(), answer)) {
				stopIndiceTimer();
				stopTimeoutTimer();
				event.write(IRCUtils.bold("Bien joué " + event.getAuthor()
						+ " ! ")
						+ "La réponse était: "
						+ answer
						+ ". Prochaine question dans quelques secondes ...");
				dao.giveOnePoint(event.getAuthor());
				nextQuestion(event, 10);
			}
		}
	}

	public boolean matches(String proposition, String answer) {

		// check if those are numbers
		try {
			double p = Double.parseDouble(proposition.replace(" ", ""));
			double a = Double.parseDouble(answer.replace(" ", ""));
			if (p == a) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			// not numbers, continue
		}

		proposition = normalize(proposition);
		answer = normalize(answer);

		// check if we have an exact match after normalization
		if (StringUtils.equals(proposition, answer)) {
			return true;
		}

		// be permissive regarding the response (experimental)
		if (answer.length() >= 4) {
			int diff = (answer.length() / 4);
			if (StringUtils.getLevenshteinDistance(proposition, answer) <= diff) {
				return true;
			}
		}

		return false;

	}

	public String computeIndice(String answer) {
		int ratio = 3;

		char c = '.';
		int length = answer.length();
		Random random = new Random();

		char[] indice = new char[length];
		Arrays.fill(indice, c);

		// fill spaces
		for (int i = 0; i < length; i++) {
			if (answer.charAt(i) == ' ') {
				indice[i] = ' ';
			}
		}

		// add some letters
		for (int i = 0; i < length / ratio + 1; i++) {
			int rand = random.nextInt(length);
			// if spot is already filled, choose next available spot
			while (indice[rand] != c) {
				rand = (rand + 1) % length;
			}
			indice[rand] = answer.charAt(rand);
		}

		return new String(indice);
	}

	private String normalize(String source) {
		source = StringUtils.trimToEmpty(stripAccents(source)).toUpperCase();
		source.replace("L'", "");

		List<String> dest = Lists.newArrayList();
		for (String word : Arrays.asList(source.split(" "))) {
			boolean isStopword = false;
			for (String stopword : STOPWORDS) {
				if (stopword.equalsIgnoreCase(word)) {
					isStopword = true;
					break;
				}
			}
			if (!isStopword) {
				dest.add(word);
			}
		}
		return StringUtils.join(dest, " ");
	}

	private String stripAccents(String source) {
		source = Normalizer.normalize(source, Normalizer.Form.NFD);
		source = source.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return source;
	}

	@SuppressWarnings("unchecked")
	private List<String> getQuestions() throws IOException {
		List<String> lines = state.get(QUESTIONS, List.class);
		if (lines == null) {
			lines = IOUtils.readLines(
					getClass().getResourceAsStream("/quizz.txt"), "ISO-8859-1");
			state.put(QUESTIONS, lines);
		}
		return lines;
	}

	@Trigger("!score")
	public String score(TriggerEvent event) {
		return IRCUtils.bold("Scoreboard: ")
				+ bundleService.getWebServerRootPath() + "/quizz";
	}

	@ServletMethod("/quizz")
	public void scoreboard(ServletEvent event) throws Exception {
		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();
		request.setAttribute("players", dao.getPlayersOrderByPoints());
		request.getRequestDispatcher("/scoreboard.jsp").forward(request,
				response);

	}

}
