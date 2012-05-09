package be.hehehe.geekbot.bot;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.ServletMethod;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;

import com.google.common.collect.Lists;

/**
 * Utility extension for scanning triggers and actions.
 * 
 * 
 */
public class GeekBotCDIExtension implements Extension {

	List<Method> triggers = Lists.newArrayList();
	List<Method> randoms = Lists.newArrayList();
	List<Method> timers = Lists.newArrayList();
	List<Method> servletMethods = Lists.newArrayList();

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		Class<?> klass = pat.getAnnotatedType().getJavaClass();
		if (klass.isAnnotationPresent(BotCommand.class)) {
			for (Method m : klass.getMethods()) {
				if (m.isAnnotationPresent(Trigger.class)) {
					triggers.add(m);
				}
				if (m.isAnnotationPresent(RandomAction.class)) {
					randoms.add(m);
				}
				if (m.isAnnotationPresent(TimedAction.class)) {
					timers.add(m);
				}
				if (m.isAnnotationPresent(ServletMethod.class)) {
					servletMethods.add(m);
				}
			}
		}
	}

	public List<Method> getTriggers() {
		return triggers;
	}

	public List<Method> getRandoms() {
		return randoms;
	}

	public List<Method> getTimers() {
		return timers;
	}

	public List<Method> getServletMethods() {
		return servletMethods;
	}
}
