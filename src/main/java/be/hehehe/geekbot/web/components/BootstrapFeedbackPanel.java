package be.hehehe.geekbot.web.components;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.bootstrap.Bootstrap;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import be.hehehe.geekbot.web.utils.WicketUtils;

public class BootstrapFeedbackPanel extends FeedbackPanel {

	private static final long serialVersionUID = -4454548249075569147L;

	public BootstrapFeedbackPanel(String id) {
		super(id);
		init();
	}

	public BootstrapFeedbackPanel(String id, IFeedbackMessageFilter filter) {
		super(id, filter);
		init();
	}

	private void init() {
		add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = -2728496420265562466L;

			@Override
			public String getObject() {
				StringBuilder sb = new StringBuilder();
				if (anyMessage()) {
					sb.append(" bs-fb alert");
				}
				if (anyErrorMessage()) {
					sb.append(" alert-error");
				} else {
					sb.append(" alert-success");
				}
				return sb.toString();
			}
		}));

		get("feedbackul").add(new AttributeAppender("class", " unstyled"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		Bootstrap.renderHeadResponsive(response);
		WicketUtils.loadCSS(response, BootstrapFeedbackPanel.class);
	}

}
