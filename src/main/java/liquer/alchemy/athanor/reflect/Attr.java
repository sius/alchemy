package liquer.alchemy.athanor.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Attr {
	/** the member name */
	String value() default "";
	boolean source() default true;
	boolean drain() default true;
	boolean notNull() default false;
}
