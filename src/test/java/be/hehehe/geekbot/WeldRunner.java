package be.hehehe.geekbot;

import org.jboss.weld.environment.se.Weld;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class WeldRunner extends BlockJUnit4ClassRunner {
	private final Class<?> klass;

	public WeldRunner(final Class<?> klass) throws InitializationError {
		super(klass);
		this.klass = klass;
		Main.beanManager = new Weld().initialize().getBeanManager();
	}

	@Override
	protected Object createTest() throws Exception {
		return Main.getBean(klass);
	}
}
