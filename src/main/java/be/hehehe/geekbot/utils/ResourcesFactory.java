package be.hehehe.geekbot.utils;

import java.util.Random;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

@Singleton
public class ResourcesFactory {

	private Random random;

	@Produces
	public Logger getLogger(InjectionPoint ip) {
		return Logger.getLogger(ip.getMember().getDeclaringClass());
	}

	@Produces
	public Random getRandom() {
		if (random == null) {
			random = new Random();
		}
		return random;
	}
}
