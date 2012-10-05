package be.hehehe.geekbot;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;

import be.hehehe.geekbot.web.WicketApplication;

public class WicketTestApplication extends WicketApplication {

	private BeanManager beanManager;

	public WicketTestApplication(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	@Override
	protected void setupCDI() {
		new CdiConfiguration(beanManager).setPropagation(
				ConversationPropagation.NONE).configure(this);
	}

}
