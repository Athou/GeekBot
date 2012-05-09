package be.hehehe.geekbot.web.auth;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class LoggedOutButtonPanel extends Panel {

	public LoggedOutButtonPanel(String id) {
		super(id);
		add(new Link<String>("login") {
			@Override
			public void onClick() {
				setResponsePage(LoginPage.class);
			}
		});

	}

}
