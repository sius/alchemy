package liquer.alchemy.athanor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MethodSource implements ISource {

	private static String leftCut(String value, String... prefixes) {
    	if (value == null || value.length() == 0 || prefixes == null)
    		return value;
    	for(String prefix : prefixes) {
    		if (value.indexOf(prefix) == 0) {
    			return value.substring(prefix.length());
    		} 
    	}
    	return value;
    }
	private static boolean returnsVoid(Method m) {
		return m.getReturnType().equals(void.class);
	}
	private static boolean withoutParams(Method m) {
		return m.getParameterTypes().length == 0;
	}
	private static boolean isValid(Method m) {
		return !returnsVoid(m) 
			&& withoutParams(m) 
			&& !Modifier.isStatic(m.getModifiers()) 
			&& !Modifier.isAbstract(m.getModifiers());
	}
	
	public static ISource getInstance(Method method) {
		if (method != null) {
			Attr attr = method.getAnnotation(Attr.class);
			if (attr != null && attr.source() && isValid(method)) {
				return new MethodSource(method,
					attr.value().length() > 0
						? attr.value() 
						: leftCut(method.getName(), "get", "is"),
						attr.notNull());
			}
		}
		return null;
	}

	private Method method;
	private String name;
	private boolean notNull;
	
	private MethodSource(Method method, String name, boolean notNull) {
		this.method = method;
		this.name = name;
		this.notNull = notNull;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue(Object instance) {
		boolean accessible = method.isAccessible();
		if (!accessible)
			method.setAccessible(true);
		try {
			Object m = method.invoke(instance);
			return (notNull && m == null) 
				? Athanor.mostLikelyNotNullDefault(method.getReturnType())
				: m;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// no operation
		} finally {
			method.setAccessible(accessible);
		}
		return null;
	}
}
