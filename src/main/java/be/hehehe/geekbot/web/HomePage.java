package be.hehehe.geekbot.web;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;

import be.hehehe.geekbot.utils.BundleService;

@SuppressWarnings("serial")
public class HomePage extends TemplatePage {

	@Inject
	BundleService bundleService;

	public HomePage() {
		add(new Label("bot-name", bundleService.getBotName()));
	}

	@Override
	protected String getTitle() {
		return "Home";
	}

}
