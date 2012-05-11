package be.hehehe.geekbot.web;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.WebPage;

import be.hehehe.geekbot.web.auth.LoginPage;
import be.hehehe.geekbot.web.auth.WicketSession;

public class WicketApplication extends AuthenticatedWebApplication {

	@Override
	protected void init() {
		super.init();
		mountPage("login", LoginPage.class);
		mountPage("logout", SignOutPage.class);

		mountPage("quizz", QuizzScorePage.class);
		mountPage("quizzmerge", QuizzMergePage.class);

		getMarkupSettings().setStripWicketTags(true);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return WicketSession.class;
	}
}
