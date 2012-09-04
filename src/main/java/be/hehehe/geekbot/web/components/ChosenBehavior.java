package be.hehehe.geekbot.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import be.hehehe.geekbot.web.components.references.ChosenReference;

public class ChosenBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		ChosenReference.render(response);
		String script = String.format("$('#%s').chosen()",
				component.getMarkupId());
		response.render(OnDomReadyHeaderItem.forScript(script));

	}
}
