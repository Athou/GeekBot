package be.hehehe.geekbot.web;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Level;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.odlabs.wiquery.core.javascript.JsScope;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.javascript.helper.EventsHelper;

import be.hehehe.geekbot.persistence.dao.LogFileDAO;

@SuppressWarnings("serial")
public class LogViewerPage extends TemplatePage {

	public static final String PARAM_LOGLEVEL = "level";

	@Inject
	LogFileDAO logDAO;

	public LogViewerPage(PageParameters params) {

		final Level level = getLevel(params);

		Form<Level> form = new StatelessForm<Level>("form", Model.of(level)) {

			private DropDownChoice<Level> levelsChoice;

			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(levelsChoice = new DropDownChoice<Level>("levels", getModel(), LogFileDAO.LEVELS));
			}

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				JsStatement st = new JsStatement().$(levelsChoice)
						.chain(EventsHelper.change(JsScope.quickScope(new JsStatement().$(this).chain(EventsHelper.submit()))));
				response.render(OnDomReadyHeaderItem.forScript(st.render()));
			}

			@Override
			protected void onSubmit() {
				super.onSubmit();
				setResponsePage(LogViewerPage.class, new PageParameters().add(PARAM_LOGLEVEL, getModelObject().toString()));
			}
		};
		add(form);

		IModel<String> logModel = new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				List<String> filteredLines = null;
				try {
					filteredLines = logDAO.getLines(level);
					if (filteredLines.isEmpty()) {
						return "Nothing to display";
					}
				} catch (IOException e) {
					return "Could not read log file: " + e.getMessage();
				}
				return StringUtils.join(filteredLines, SystemUtils.LINE_SEPARATOR);
			}
		};

		add(new Label("logwrapper", logModel));

	}

	private Level getLevel(PageParameters params) {
		String levelStr = params.get(PARAM_LOGLEVEL).toString(Level.ERROR.toString());

		Level level = null;
		for (Level l : LogFileDAO.LEVELS) {
			if (StringUtils.equals(l.toString(), levelStr)) {
				level = l;
				break;
			}
		}
		return level;
	}

	@Override
	protected String getTitle() {
		return "Latest Logs";
	}

}
