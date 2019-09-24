package liquer.alchemy.athanor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldSource implements ISource {
	
	public static ISource getInstance(Field field) {
		if (field != null) {
			Attr attr = field.getAnnotation(Attr.class);
			if (attr != null && attr.source() && !Modifier.isStatic(field.getModifiers()) && !Modifier.isAbstract(field.getModifiers())) {
				return new FieldSource(field, 
					attr.value().length() > 0
						? attr.value() :
						field.getName(),
						attr.notNull());
			}
		}
		return null;
	}

	private Field field;
	private String name;
	private boolean notNull;
	
	private FieldSource(Field field, String name, boolean notNull) {
		this.field = field;
		this.name = name;
		this.notNull = notNull;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue(Object instance) {
		boolean accessible = field.isAccessible();
		if (!accessible)
			field.setAccessible(true);
		try {
			Object f = field.get(instance);
			return (notNull && f == null) 
				? Athanor.mostLikelyNotNullDefault(field.getType())
				: f;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// no operation
		} finally {
			field.setAccessible(accessible);
		}
		return null;
	}
}
