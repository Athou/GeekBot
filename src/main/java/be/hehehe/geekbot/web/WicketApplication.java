package be.hehehe.geekbot.web;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.markup.html.WebPage;

import be.hehehe.geekbot.web.auth.LoginPage;
import be.hehehe.geekbot.web.auth.LogoutPage;
import be.hehehe.geekbot.web.auth.WicketSession;

public class WicketApplication extends AuthenticatedWebApplication {

	@Override
	protected void init() {
		super.init();
		setupCDI();

		mountPage("login", LoginPage.class);
		mountPage("logout", LogoutPage.class);

		mountPage("help", HelpPage.class);

		mountPage("quizz", QuizzScorePage.class);
		mountPage("quizzmerge", QuizzMergePage.class);

		mountPage(String.format("log/#{%s}", LogViewerPage.PARAM_LOGLEVEL),
				LogViewerPage.class);

		getMarkupSettings().setStripWicketTags(true);
	}

	protected void setupCDI() {
		try {
			BeanManager beanManager = (BeanManager) new InitialContext()
					.lookup("java:comp/BeanManager");
			new CdiConfiguration(beanManager).setPropagation(
					ConversationPropagation.NONE).configure(this);
		} catch (NamingException e) {
			throw new IllegalStateException("Unable to obtain CDI BeanManager",
					e);
		}
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
