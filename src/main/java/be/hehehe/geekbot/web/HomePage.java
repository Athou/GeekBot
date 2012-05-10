package be.hehehe.geekbot.web;

import org.apache.wicket.markup.html.basic.Label;

@SuppressWarnings("serial")
public class HomePage extends TemplatePage {

	public HomePage() {
		add(new Label("bot-name", bundleService.getBotName()));
	}

	@Override
	protected String getTitle() {
		return "Home";
	}

}
