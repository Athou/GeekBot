package be.hehehe.geekbot.web;

import javax.inject.Inject;

import org.apache.wicket.bootstrap.Bootstrap;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.web.auth.LoggedInButtonPanel;
import be.hehehe.geekbot.web.auth.LoggedOutButtonPanel;
import be.hehehe.geekbot.web.auth.WicketSession;
import be.hehehe.geekbot.web.nav.NavigationHeader;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@SuppressWarnings("serial")
public abstract class TemplatePage extends WebPage {

	@Inject
	BundleService bundleService;

	public TemplatePage() {

		add(new Label("title", getTitle()));
		add(new Label("project-name", bundleService.getBotName()));

		String buttonId = "topright-button";
		if (getAuthSession().isSignedIn()) {
			add(new LoggedInButtonPanel(buttonId));
		} else {
			add(new LoggedOutButtonPanel(buttonId));
		}

		addNavigationMenu();

	}

	private void addNavigationMenu() {
		Multimap<String, PageModel> pages = LinkedListMultimap.create();
		pages.put("Home", new PageModel("Home Page", HomePage.class));
		pages.put("Quizz", new PageModel("Scoreboard", QuizzScorePage.class));
		pages.put("Quizz",
				new PageModel("Merge Requests", QuizzMergePage.class));
		pages.put("Help", new PageModel("Triggers", HelpPage.class));
		pages.put("Debug", new PageModel("View Logs", LogViewerPage.class));

		RepeatingView repeatingView = new RepeatingView("nav-headers");

		for (String category : pages.keySet()) {
			repeatingView.add(new NavigationHeader(repeatingView.newChildId(),
					category, pages.get(category)));
		}
		add(repeatingView);

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(Bootstrap.responsive()));
		response.render(JavaScriptHeaderItem
				.forReference(new JavaScriptResourceReference(
						TemplatePage.class, TemplatePage.class.getSimpleName()
								+ ".js")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(
				TemplatePage.class, TemplatePage.class.getSimpleName() + ".css")));
	}

	protected abstract String getTitle();

	public static class PageModel {
		private String name;
		private Class<? extends TemplatePage> pageClass;

		public PageModel(String name, Class<? extends TemplatePage> pageClass) {
			super();
			this.name = name;
			this.pageClass = pageClass;
		}

		public Class<? extends TemplatePage> getPageClass() {
			return pageClass;
		}

		public String getName() {
			return name;
		}

	}

	public WicketSession getAuthSession() {
		return (WicketSession) super.getSession();
	}
}
