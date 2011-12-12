package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotate a method (from a class annotated with @BotCommand) to indicate it is
 * a trigger. A trigger method can return a String or a List<String>.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Trigger {
	public String value() default "";

	public TriggerType type() default be.hehehe.geekbot.annotations.TriggerType.EXACTMATCH;
}
