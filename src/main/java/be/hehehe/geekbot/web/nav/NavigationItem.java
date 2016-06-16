package be.hehehe.geekbot.web.nav;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import be.hehehe.geekbot.web.TemplatePage;
import be.hehehe.geekbot.web.TemplatePage.PageModel;

@SuppressWarnings("serial")
public class NavigationItem extends Panel {

	public NavigationItem(String id, PageModel page) {
		super(id);
		final Class<? extends TemplatePage> pageClass = page.getPageClass();

		add(new AttributeModifier("class", new AbstractReadOnlyModel<String>() {
			public String getObject() {
				return getPage().getClass().equals(pageClass) ? "active" : AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE;
			}
		}));

		BookmarkablePageLink<TemplatePage> pageLink = new BookmarkablePageLink<TemplatePage>("a", page.getPageClass());
		add(pageLink);

		pageLink.add(new Label("link-name", page.getName()));

	}
}
