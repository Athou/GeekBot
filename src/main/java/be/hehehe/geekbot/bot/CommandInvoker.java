package be.hehehe.geekbot.bot;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import javax.ejb.AccessTimeout;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.log4j.Logger;

@Singleton
public class CommandInvoker {

	@Inject
	Logger log;

	@Inject
	Instance<Object> container;

	@Asynchronous
	@Lock(LockType.READ)
	@AccessTimeout(-1)
	public Future<Object> invoke(Method method, Object... args) {
		Object result = null;

		log.debug("Invoking: " + method.getDeclaringClass().getSimpleName()
				+ "#" + method.getName());

		Object instance = container.select(method.getDeclaringClass()).get();

		final Object commandInstance = instance;

		try {
			if (method.getParameterTypes().length == 0) {
				result = method.invoke(commandInstance, new Object[0]);
			} else {
				result = method.invoke(commandInstance, args);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Done invoking: "
				+ method.getDeclaringClass().getSimpleName() + "#"
				+ method.getName());
		return new AsyncResult<Object>(result);
	}
}
