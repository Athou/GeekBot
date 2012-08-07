package be.hehehe.geekbot;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.geekbot.bot.GeekBot;

public class Main {

	static BeanManager beanManager;

	public static void main(String[] args) {
		WeldContainer container = new Weld().initialize();
		beanManager = container.getBeanManager();
		container.instance().select(GeekBot.class).get();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<? extends T> klass) {
		Set<Bean<?>> beans = beanManager.getBeans(klass);
		Bean<?> bean = beanManager.resolve(beans);
		CreationalContext<?> creationalContext = beanManager
				.createCreationalContext(bean);
		T result = (T) beanManager.getReference(bean, klass, creationalContext);
		return result;
	}

}
