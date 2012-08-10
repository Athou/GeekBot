package be.hehehe.geekbot.web;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;

import be.hehehe.geekbot.web.auth.LoginPage;
import be.hehehe.geekbot.web.auth.LogoutPage;
import be.hehehe.geekbot.web.auth.WicketSession;

public class WicketApplication extends AuthenticatedWebApplication {

	@Override
	protected void init() {
		super.init();
		mountPage("login", LoginPage.class);
		mountPage("logout", LogoutPage.class);

		mountPage("quizz", QuizzScorePage.class);
		mountPage("quizzmerge", QuizzMergePage.class);

		mountPage("log", LogViewerPage.class);

		getMarkupSettings().setStripWicketTags(true);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return WicketSession.class;
	}

	public static WicketApplication get() {
		return (WicketApplication) Application.get();
	}
}
