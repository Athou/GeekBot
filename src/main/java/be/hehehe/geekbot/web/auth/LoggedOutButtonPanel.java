package be.hehehe.geekbot.web.auth;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class LoggedOutButtonPanel extends Panel {

	public LoggedOutButtonPanel(String id) {
		super(id);
		add(new BookmarkablePageLink<String>("login", LoginPage.class));
	}
}
