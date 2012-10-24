package be.hehehe.geekbot.web.components.references;

import java.util.Arrays;

import org.apache.wicket.bootstrap.Bootstrap;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class ChosenReference extends JavaScriptResourceReference {

	private static final long serialVersionUID = 1L;

	private static ChosenReference instance = new ChosenReference();

	public ChosenReference() {
		super(ChosenReference.class, "chosen.jquery.min.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies() {
		return Arrays.<HeaderItem> asList(JavaScriptHeaderItem
				.forReference(Bootstrap.responsive()), CssHeaderItem
				.forReference(new CssResourceReference(ChosenReference.class,
						"chosen.css")));
	}

	public static void render(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem.forReference(instance));
	}

}
