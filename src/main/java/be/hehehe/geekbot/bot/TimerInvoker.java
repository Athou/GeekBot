package be.hehehe.geekbot.bot;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import be.hehehe.geekbot.annotations.TimedAction;

@Singleton
public class TimerInvoker {

	@Inject
	Logger log;

	@Inject
	GeekBot bot;

	private Map<Method, Long> timers = Maps.newConcurrentMap();

	public void setTimers(List<Method> methods) {
		Long now = System.currentTimeMillis();
		for (Method method : methods) {
			timers.put(method, now);
		}
	}

	@Schedule(hour = "*", minute = "*", persistent = false)
	public void timer() {
		Long now = System.currentTimeMillis();
		for (final Method method : timers.keySet()) {
			int interval = method.getAnnotation(TimedAction.class).value();
			TimeUnit timeUnit = method.getAnnotation(TimedAction.class)
					.timeUnit();

			if (now - timers.get(method) > timeUnit.toMillis(interval)) {
				bot.invokeTrigger(method);
			}

		}
	}
}