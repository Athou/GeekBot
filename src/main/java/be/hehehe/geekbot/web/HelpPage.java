package be.hehehe.geekbot.web;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;

import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.annotations.Triggers;
import be.hehehe.geekbot.web.utils.WicketUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class HelpPage extends TemplatePage {

	@Inject
	@Triggers
	List<Method> triggers;

	public HelpPage() {
		ListView<TriggerModel> view = new ListView<TriggerModel>("class",
				new MappedTriggerModel()) {

			@Override
			protected void populateItem(ListItem<TriggerModel> item) {
				TriggerModel model = item.getModelObject();
				item.add(new Label("name", model.getKlass().getSimpleName()));
				item.add(new ListView<Method>("trigger", model.getTriggers()) {

					@Override
					protected void populateItem(ListItem<Method> item) {
						Method method = item.getModelObject();
						Trigger trigger = method.getAnnotation(Trigger.class);
						Help help = method.getAnnotation(Help.class);
						item.add(new Label("name", trigger.value()));
						item.add(new Label(
								"type",
								trigger.type() == TriggerType.STARTSWITH ? "starts with"
										: "exact match"));
						item.add(new Label("desc",
								help == null ? "No description." : help.value()));
					}
				});
			}
		};
		add(view);
	}

	@Override
	protected String getTitle() {
		return "Help";
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		WicketUtils.loadCSS(response, HelpPage.class);
	}

	private class MappedTriggerModel extends
			LoadableDetachableModel<List<TriggerModel>> {

		@Override
		protected List<TriggerModel> load() {
			Map<Class<?>, List<Method>> map = Maps.newHashMap();
			for (Method method : triggers) {
				Trigger trigger = method.getAnnotation(Trigger.class);
				if (trigger.type() == TriggerType.EXACTMATCH
						|| trigger.type() == TriggerType.STARTSWITH) {
					List<Method> list = map.get(method.getDeclaringClass());
					if (list == null) {
						list = Lists.newArrayList();
						map.put(method.getDeclaringClass(), list);
					}
					list.add(method);
				}
			}

			List<TriggerModel> list = Lists.newArrayList();
			for (Class<?> key : map.keySet()) {
				List<Method> methods = map.get(key);
				Collections.sort(methods, new Comparator<Method>() {
					@Override
					public int compare(Method o1, Method o2) {
						return o1
								.getAnnotation(Trigger.class)
								.value()
								.compareTo(
										o2.getAnnotation(Trigger.class).value());
					}
				});
				list.add(new TriggerModel(key, methods));
			}

			Collections.sort(list, new Comparator<TriggerModel>() {

				@Override
				public int compare(TriggerModel o1, TriggerModel o2) {
					return o1.getKlass().getSimpleName()
							.compareTo(o2.getKlass().getSimpleName());
				}
			});
			return list;
		}
	}

	private static class TriggerModel implements Serializable {
		private Class<?> klass;
		private List<Method> triggers;

		public TriggerModel(Class<?> klass, List<Method> triggers) {
			this.klass = klass;
			this.triggers = triggers;
		}

		public Class<?> getKlass() {
			return klass;
		}

		public List<Method> getTriggers() {
			return triggers;
		}

	}

}
