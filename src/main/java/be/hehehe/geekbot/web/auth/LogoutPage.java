package be.hehehe.geekbot.web.auth;

import be.hehehe.geekbot.web.TemplatePage;

@SuppressWarnings("serial")
public class LogoutPage extends TemplatePage {

	public LogoutPage() {
		getSession().invalidate();
		setResponsePage(getApplication().getHomePage());
	}

	@Override
	protected String getTitle() {
		return "Logout";
	}

}
