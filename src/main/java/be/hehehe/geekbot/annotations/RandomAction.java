package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Will execute this method randomly when someone speaks on the chan. The value
 * is a proc percentage (default: 1%).
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RandomAction {
	/**
	 * Proc percentage (default: 1%).
	 * 
	 */
	public int value() default 1;
}