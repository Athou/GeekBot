package be.hehehe.geekbot.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class Quizz implements EntryPoint {

	public static final EventBus eventBus = GWT.create(SimpleEventBus.class);

	public void onModuleLoad() {

		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				System.out.println(e.getMessage());
			}
		});

		RootPanel.get("players").add(new QuizzPlayersPanel());
		RootPanel.get("requests").add(new QuizzMergeRequestsPanel());

	}

	public static interface RefreshHandler extends EventHandler {
		void onRefreshNeeded(RefreshEvent refreshEvent);
	}

	public static class RefreshEvent extends GwtEvent<RefreshHandler> {

		public static Type<RefreshHandler> TYPE = new Type<RefreshHandler>();

		@Override
		public Type<RefreshHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RefreshHandler handler) {
			handler.onRefreshNeeded(this);
		}
	}

}
