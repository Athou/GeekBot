package be.hehehe.geekbot.web.auth;

import be.hehehe.geekbot.web.TemplatePage;

@SuppressWarnings("serial")
public class LoginPage extends TemplatePage {

	public LoginPage() {
		add(new LoginPanel("login"));
	}

	@Override
	protected String getTitle() {
		return "Login";
	}

}
