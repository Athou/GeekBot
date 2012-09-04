package be.hehehe.geekbot.utils;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Logger;

@ApplicationScoped
public class ResourcesFactory {

	private Random random = new Random();

	@Produces
	public Logger getLogger(InjectionPoint ip) {
		return Logger.getLogger(ip.getMember().getDeclaringClass());
	}

	@Produces
	public Random getRandom() {
		return random;
	}
}
