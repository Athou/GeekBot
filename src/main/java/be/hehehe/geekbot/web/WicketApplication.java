package be.hehehe.geekbot.web;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class WicketApplication extends WebApplication {

	@Override
	protected void init() {
		super.init();
		mountPage("quizz", QuizzScorePage.class);
		mountPage("quizzmerge", QuizzMergePage.class);

		getMarkupSettings().setStripWicketTags(true);

	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
}
