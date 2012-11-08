package be.hehehe.geekbot.web.utils;

import org.apache.wicket.model.PropertyModel;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.argument.Argument;
import ch.lambdaj.function.argument.ArgumentsFactory;

public class ModelFactory {

	public static <T> PropertyModel<T> model(Object value, T proxiedValue) {
		Argument<T> a = ArgumentsFactory.actualArgument(proxiedValue);
		String invokedPN = a.getInkvokedPropertyName();
		PropertyModel<T> m = new PropertyModel<T>(value, invokedPN);
		return m;
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxy(T t) {
		Object object = Lambda.on(t.getClass());
		return (T) object;
	}

	public static <T> T proxy(Class<T> clazz) {
		return Lambda.on(clazz);
	}
}