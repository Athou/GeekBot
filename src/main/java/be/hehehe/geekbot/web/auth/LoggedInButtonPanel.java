package be.hehehe.geekbot.web.auth;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class LoggedInButtonPanel extends Panel {

	public LoggedInButtonPanel(String id) {
		super(id);
		add(new Label("username", (String) getSession().getAttribute("name")));
		add(new BookmarkablePageLink<String>("signout", LogoutPage.class));
	}

}
