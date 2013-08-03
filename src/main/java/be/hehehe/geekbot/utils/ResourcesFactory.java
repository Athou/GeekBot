package be.hehehe.geekbot.utils;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@ApplicationScoped
public class ResourcesFactory {

	@Inject
	BundleService bundle;

	private Random random = new Random();

	@Produces
	public Logger getLogger(InjectionPoint ip) {
		return Logger.getLogger(ip.getMember().getDeclaringClass());
	}

	@Produces
	public Random getRandom() {
		return random;
	}

	@Produces
	public Twitter getTwitter() {
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(bundle.getTwitterConsumerKey(),
				bundle.getTwitterConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(bundle.getTwitterToken(),
				bundle.getTwitterTokenSecret()));
		return twitter;
	}
}
