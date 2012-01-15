package be.hehehe.geekbot.bot;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import com.google.common.collect.Maps;

@Singleton
public class StateProducer {

	private Map<String, State> states = Maps.newHashMap();

	@Produces
	public State getState(InjectionPoint injectionPoint) {
		String className = injectionPoint.getMember().getDeclaringClass()
				.getName();
		State state = states.get(className);
		if (state == null) {
			state = new StateImpl();
			states.put(className, state);
		}
		return state;
	}
}
