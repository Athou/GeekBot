package be.hehehe.geekbot.commands;

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
	private static final String CURRENT_QUESTION = "current-question";
	private static final String CURRENT_ANSWER = "current-answer";
	private static final String CURRENT_TIMER = "current-timer";

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
		state.put(ENABLED, Boolean.FALSE);
		event.write("Quizz stopped.");
	}

	@Trigger(value = "!tg")
	@Help("Stops the quizz.")
	public void stopQuizz2(TriggerEvent event) {
		stopQuizz(event);
	}

	private void nextQuestion(final TriggerEvent event, int delay) {
		state.put(CURRENT_QUESTION, null);
		state.put(CURRENT_ANSWER, null);
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					List<String> lines = IOUtils.readLines(getClass()
							.getResourceAsStream("/quizz.txt"), "ISO-8859-1");
					int rand = new Random().nextInt(lines.size());

					String line = lines.get(rand);
					String[] split = line.split("\\\\");

					String question = split[0].trim();
					String answer = split[1].trim();

					state.put(CURRENT_QUESTION, question);
					state.put(CURRENT_ANSWER, answer);

					event.write(IRCUtils.bold("Question! ") + question);

					startTimeoutTimer(event);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		};
		event.getScheduler().schedule(thread, delay, TimeUnit.SECONDS);

	}

	private void startTimeoutTimer(final TriggerEvent event) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				event.write(IRCUtils.bold("Trop tard!") + "La réponse était: "
						+ state.get(CURRENT_ANSWER)
						+ ". Prochaine question dans quelques secondes ...");
				nextQuestion(event, 5);
			}
		};
		ScheduledFuture<?> timer = event.getScheduler().schedule(thread, 30,
				TimeUnit.SECONDS);
		state.put(CURRENT_TIMER, timer);
	}

	@Trigger(type = TriggerType.EVERYTHING)
	public void handlePossibleAnswer(TriggerEvent event) {
		String answer = state.get(CURRENT_ANSWER, String.class);
		if (answer != null && Boolean.TRUE.equals(state.get(ENABLED))
				&& !event.isStartsWithTrigger()) {
			if (matches(event.getMessage(), answer)) {
				event.write(IRCUtils.bold("Bien joué " + event.getAuthor()
						+ " ! ")
						+ "La réponse était: "
						+ answer
						+ ". Prochaine question dans quelques secondes");

				// cancel timer
				ScheduledFuture<?> timer = state.get(CURRENT_TIMER,
						ScheduledFuture.class);
				if (timer != null) {
					timer.cancel(true);
				}

				nextQuestion(event, 5);
			}
		}
	}

	public boolean matches(String proposition, String answer) {
		boolean match = false;

		proposition = StringUtils.trimToEmpty(stripAccents(proposition))
				.toUpperCase();
		answer = StringUtils.trimToEmpty(stripAccents(answer)).toUpperCase();

		// check if those are numbers
		try {
			double p = Double.parseDouble(proposition.replace(" ", ""));
			double a = Double.parseDouble(answer.replace(" ", ""));
			match |= (p == a);
		} catch (Exception e) {
			// do nothing
		}

		// be permissive regarding the response (experimental)
		match |= (StringUtils.getLevenshteinDistance(proposition, answer) <= 1);

		return match;

	}

	private String stripAccents(String source) {
		source = Normalizer.normalize(source, Normalizer.Form.NFD);
		return source.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}
