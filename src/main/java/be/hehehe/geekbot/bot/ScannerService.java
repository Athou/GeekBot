package be.hehehe.geekbot.bot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.RandomAction;
import be.hehehe.geekbot.annotations.TimedAction;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.utils.BundleService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * utility methods for scanning triggers and actions.
 * 
 * 
 */
@Named
public class ScannerService {

	@Inject
	BundleService bundleService;

	@Inject
	WeldContainer container;

	/**
	 * Returns a list of methods annotated with the @Trigger annotation in
	 * classes with the @BotCommand annotation.
	 * 
	 */
	public List<Method> scanTriggers() {
		List<Method> methods = Lists.newArrayList();
		for (Class<?> klass : getAnnotatedClasses(BotCommand.class)) {
			for (Method m : klass.getMethods()) {
				if (m.isAnnotationPresent(Trigger.class)) {
					methods.add(m);
				}
			}
		}
		return methods;
	}

	/**
	 * Returns a list of methods annotated with the @randomAction annotation in
	 * classes with the @BotCommand annotation.
	 * 
	 */
	public List<Method> scanRandom() {
		List<Method> random = Lists.newArrayList();
		for (Class<?> klass : getAnnotatedClasses(BotCommand.class)) {
			for (Method m : klass.getMethods()) {
				if (m.isAnnotationPresent(RandomAction.class)) {
					random.add(m);
				}
			}
		}
		return random;
	}

	/**
	 * Returns a list of methods annotated with the @TimedAction annotation in
	 * classes with the @BotCommand annotation.
	 * 
	 */
	public List<Method> scanTimers() {
		List<Method> timers = Lists.newArrayList();
		for (Class<?> klass : getAnnotatedClasses(BotCommand.class)) {
			for (Method m : klass.getMethods()) {
				if (m.isAnnotationPresent(TimedAction.class)) {
					timers.add(m);
				}
			}
		}
		return timers;
	}

	@SuppressWarnings("serial")
	private Set<Class<?>> getAnnotatedClasses(
			Class<? extends Annotation> annotation) {
		Set<Class<?>> set = Sets.newHashSet();
		Set<Bean<?>> beans = container.getBeanManager().getBeans(Object.class,
				new AnnotationLiteral<Default>() {

				});
		for (Bean<?> bean : beans) {
			Class<?> klass = bean.getBeanClass();
			if (klass.isAnnotationPresent(annotation)) {
				set.add(klass);
			}
		}

		return set;
	}

}
