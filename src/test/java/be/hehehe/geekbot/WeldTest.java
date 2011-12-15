package be.hehehe.geekbot;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class WeldTest {
	private static WeldContainer container = new Weld().initialize();

	protected static <T> T lookup(Class<T> klass) {
		return container.instance().select(klass).get();
	}
}
