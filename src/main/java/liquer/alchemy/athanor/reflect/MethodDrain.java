package liquer.alchemy.athanor.reflect;

import java.lang.reflect.*;

public final class MethodDrain implements IDrain{

	public static IDrain getInstance(Method method) {
		if (method != null && !Modifier.isStatic(method.getModifiers()) 
				&& !Modifier.isAbstract(method.getModifiers()) 
				&& method.getReturnType().equals(void.class) 
				&& method.getParameterTypes().length == 1 
				&& method.getName().startsWith("set")) {
			Attr attr = method.getAnnotation(Attr.class);
			return new MethodDrain(method, attr == null || attr.value().length() == 0 ? method.getName().substring(3) : attr.value());
		}
		return null;
	}
	private Method method;
	private String name;
	private Class<?> type;
	
	private MethodDrain(Method method, String name) {
		this.method = method;
		this.name = name;
		this.type = method.getParameterTypes()[0];
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setValue(Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
		boolean accessible = method.isAccessible();
		if (!accessible)
			method.setAccessible(true);
		method.invoke(instance, value);
		method.setAccessible(accessible);
	}
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public Class<?> getGenericType() {
		Type[] types = method.getGenericParameterTypes();
		Class<?> itemClass = null;
		if (types.length > 0 && types[0] instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) types[0];
			itemClass = Athanor.getGenericType(pt);
		}
		return itemClass;
	}
}
