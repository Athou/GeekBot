package be.hehehe.geekbot.web.nav;

import java.util.Collection;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import be.hehehe.geekbot.web.TemplatePage.PageModel;

@SuppressWarnings("serial")
public class NavigationHeader extends Panel {

	public NavigationHeader(String id, String headerName,
			Collection<PageModel> pages) {
		super(id);
		add(new Label("header", headerName));

		RepeatingView repeatingView = new RepeatingView("li");
		for (PageModel page : pages) {
			repeatingView.add(new NavigationItem(repeatingView.newChildId(),
					page));
		}
		add(repeatingView);
	}

}
