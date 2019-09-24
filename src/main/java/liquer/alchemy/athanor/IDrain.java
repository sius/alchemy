package liquer.alchemy.athanor;

import java.lang.reflect.InvocationTargetException;

public interface IDrain {
	String getName();
	void setValue(Object instance, Object value) throws IllegalAccessException, InvocationTargetException;
	Class<?> getType();
	Class<?> getGenericType();
}
