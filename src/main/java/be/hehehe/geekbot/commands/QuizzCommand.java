package be.hehehe.geekbot.commands;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.bot.State;
import be.hehehe.geekbot.bot.TriggerEvent;
import be.hehehe.geekbot.utils.IRCUtils;

@BotCommand
public class QuizzCommand {

	@Inject
	State state;

	@Inject
	Logger log;

	private static final String ENABLED = "enabled";
	private static final String QUESTIONS = "questions";
	private static final String CURRENT_ANSWER = "current-answer";
	private static final String TIMEOUT_TIMER = "timeout-timer";
	private static final String NEXTQUESTION_TIMER = "nextquestion-timer";

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
				stopTimeoutTimer();
				event.write(IRCUtils.bold("Bien joué " + event.getAuthor()
						+ " ! ")
						+ "La réponse était: "
						+ answer
						+ ". Prochaine question dans quelques secondes ...");
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

	private String normalize(String source) {
		return StringUtils.trimToEmpty(stripAccents(source)).toUpperCase();
	}

	private String stripAccents(String source) {
		source = Normalizer.normalize(source, Normalizer.Form.NFD);
		return source.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
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

}
