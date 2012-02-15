package be.hehehe.geekbot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate any method with the right signature (i.e. HttpServletRequest
 * request, HttpServletResponse response) in a @BotCommand class to enable its
 * servlet capabilities.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServletMethod {
	public String value();

}
