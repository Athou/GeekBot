package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate any method in a @BotCommand annotated class with this annotation to
 * enable its servlet capabilities. The method either accepts a ServletEvent
 * object as argument or nothing at all. The method should not return anything
 * as the returned value will be discarded.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServletMethod {
	public String value();

}
