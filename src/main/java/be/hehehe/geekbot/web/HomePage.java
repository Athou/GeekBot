package be.hehehe.geekbot.web;

import org.apache.wicket.markup.html.basic.Label;

import be.hehehe.geekbot.utils.BundleService;

@SuppressWarnings("serial")
public class HomePage extends TemplatePage {

	public HomePage() {
		add(new Label("bot-name", getBean(BundleService.class).getBotName()));
	}

	@Override
	protected String getTitle() {
		return "Home";
	}

}
