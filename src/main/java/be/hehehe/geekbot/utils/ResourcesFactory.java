package be.hehehe.geekbot.utils;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@ApplicationScoped
public class ResourcesFactory {

	@Inject
	BundleService bundle;

	private Random random = new Random();
	private Twitter twitter;

	@Produces
	public Random getRandom() {
		return random;
	}

	@Produces
	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			twitter = TwitterFactory.getSingleton();
			twitter.setOAuthConsumer(bundle.getTwitterConsumerKey(), bundle.getTwitterConsumerSecret());
			twitter.setOAuthAccessToken(new AccessToken(bundle.getTwitterToken(), bundle.getTwitterTokenSecret()));
		}
		return twitter;
	}
}
