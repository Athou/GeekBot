package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Execute a command repeatedly. Set the value in minutes. Default is 60 minutes.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimedAction {
	public int value() default 60;

	public TimeUnit timeUnit() default TimeUnit.MINUTES;
}
