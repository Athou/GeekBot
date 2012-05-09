package be.hehehe.geekbot.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;

import be.hehehe.geekbot.Main;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.web.nav.NavigationHeader;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

@SuppressWarnings("serial")
public abstract class TemplatePage extends WebPage {
	public TemplatePage() {

		add(new Label("title", getTitle()));
		add(new Label("project-name", getBean(BundleService.class).getBotName()));

		Multimap<String, PageModel> pages = LinkedListMultimap.create();
		pages.put("Home", new PageModel("Home Page", HomePage.class));
		pages.put("Quizz", new PageModel("Scoreboard", QuizzScorePage.class));
		pages.put("Quizz",
				new PageModel("Merge Requests", QuizzMergePage.class));

		RepeatingView repeatingView = new RepeatingView("nav-headers");

		for (String category : pages.keySet()) {
			repeatingView.add(new NavigationHeader(repeatingView.newChildId(),
					category, pages.get(category)));
		}
		add(repeatingView);
	}

	protected abstract String getTitle();

	protected <T> T getBean(Class<? extends T> klass) {
		return Main.getInstance().select(klass).get();
	}

	public class PageModel {
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
}
