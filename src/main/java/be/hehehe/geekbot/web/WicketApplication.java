package be.hehehe.geekbot.web;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.WebPage;
import org.jboss.weld.wicket.util.NonContextual;
import org.jboss.weld.wicket.util.NonContextual.Instance;

import be.hehehe.geekbot.Main;
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
		getComponentInstantiationListeners().add(
				new ComponentInstantiationListener());
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

	private class ComponentInstantiationListener implements
			IComponentInstantiationListener {

		@Override
		public void onInstantiation(Component component) {
			BeanManager manager = Main.getContainer().getBeanManager();
			Instance<Component> instance = new NonContextual<Component>(
					manager, component.getClass()).existingInstance(component);
			instance.inject();
		}
	}

}
