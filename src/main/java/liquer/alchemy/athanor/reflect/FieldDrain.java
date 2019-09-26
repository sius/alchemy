package liquer.alchemy.athanor.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldDrain implements IDrain {
	
	public static IDrain getInstance(Field field) {
		if (field != null) {
			Attr attr = field.getAnnotation(Attr.class);
			if (attr != null && attr.drain() && !Modifier.isStatic(field.getModifiers()) && !Modifier.isAbstract(field.getModifiers())) {
				return new FieldDrain(field, attr.value().length() == 0 ? field.getName() : attr.value());
			}
		}
		return null;
	}
	private Field field;
	private String name;
	
	private FieldDrain(Field field, String name) {
		this.field = field;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setValue(Object instance, Object value) throws IllegalAccessException{
		boolean accessible = field.isAccessible();
		if (!accessible)
			field.setAccessible(true);
		field.set(instance, value);
		field.setAccessible(accessible);
	}
	@Override
	public Class<?> getType() {
		return field.getType();
	}
	@Override
	public Class<?> getGenericType() {
		return Athanor.getGenericType(field.getGenericType());
	}
}
