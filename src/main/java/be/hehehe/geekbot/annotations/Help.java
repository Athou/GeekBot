package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotate a method (already annotated with @Trigger) to specify its help string.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Help {

	/**
	 * The string that will be printed when help is requested.
	 * 
	 */
	public String value() default "";

}
