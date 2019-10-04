package liquer.alchemy.athanor.reflect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class Athanor {
	
	private static final Logger LOG = LogManager.getLogger(Athanor.class);
	
	private static TypeMapper typeMapper = new TypeMapper();
	
	private Function<String, String> keyMapper;
	private static final Function<String, String> IDENTITY = (i) -> i;

	private static class SingletonHelper {
		static final Athanor INSTANCE = new Athanor();
	}

	public static Athanor getInstance() {
		return SingletonHelper.INSTANCE;
	}
	public Athanor() {
		this(null);
	}
	public Athanor(Function<String, String> keyMapper) {
		this.keyMapper = keyMapper != null ? keyMapper : IDENTITY;
	}
	
	/**
	 * Returns an empty string, if aClass is assignable from String,
	 * 0, if aClass is assignable from Number,
	 * false if aClass is assignable from Boolean.
	 * Otherwise the method returns null. 
	 * @param aClass the Class
	 * @return the most likely default value
	 */
	public static Object mostLikelyNotNullDefault(Class<?> aClass) {
		if (aClass.isAssignableFrom(String.class)) return "";
		if (aClass.isAssignableFrom(Number.class)) return 0;
		if (aClass.isAssignableFrom(Boolean.class)) return false;
		return null;
	}
	
	public static Class<?> getGenericType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] actualTypeArguments = pt.getActualTypeArguments();
			if (actualTypeArguments.length > 1) {
				return (Class<?>) pt.getRawType();
			} else {
				Type actualTypeArgument = pt.getActualTypeArguments()[0];
				return (actualTypeArgument instanceof Class) 
					? (Class<?>) pt.getActualTypeArguments()[0]
					: getGenericType(pt.getActualTypeArguments()[0]);
			}
		} 
		return null;
	}
	
	public <P> P transmute(P projection, Object attributes) {
		return transmute(projection, attributes, 64);
	}
	@SuppressWarnings("unchecked")
	public <P> P transmute(P projection, Object attributes, int maxDepth) {
		Object ret = null;
		if (maxDepth > 0) {	
			if (attributes instanceof Map) {
				Map<String, Object> attributesMap = cloneMap((Map<?,?>)attributes, keyMapper);
				if (projection instanceof Map) {
					Map<String, Object> clonedProjection = cloneMap((Map<?,?>)projection, keyMapper);
					clonedProjection.putAll(attributesMap);
					ret = clonedProjection;
				} else {
//					try {
//						Constructor ctor = projection.getClass().getConstructor(Map.class);
//						ret = ctor.newInstance(attributesMap);
//					} catch (NoSuchMethodException | InstantiationException | InvocationTargetException ignore) { } catch (IllegalAccessException e) {
//					}
//					if (ret == null) {
						ret = loopObj(projection, projection.getClass(), attributesMap, maxDepth - 1);
//					}
				}
			} else if (attributes instanceof List) {
				if (projection.getClass().isArray()) {
					final List<?> attributesList = (List)attributes;
					final int lenProjection = Array.getLength(projection);
					final int totalLen = (lenProjection + attributesList.size());
					ret = Array.newInstance(projection.getClass().getComponentType(), totalLen);
					for (int i = lenProjection; i < totalLen; i++) {
						Array.set(ret, i, attributesList.get(i));
					}
				} else {
					ret = loopSeq(projection, projection.getClass(), null, (List<?>) attributes, maxDepth - 1);
				}
			} else if (attributes != null && projection != null) {
				ret = typeMapper.changeType(attributes, projection.getClass());
			}
		}
		if (ret == null) {
			return null;
		}
		return projection.getClass().isAssignableFrom(ret.getClass()) ? (P)ret : null;
	}
	
	public static Map<String, Object> cloneMap(Map<?,?> map) {
		return cloneMap(map, null); 
	}
	public static Map<String, Object> cloneMap(Map<?,?> map, Function<String, String> keyMapper) {
		Function<String, String> ekm = keyMapper != null ? keyMapper : IDENTITY;
		Map<String,Object> ret;
		try {
			Class<?> clazz = map.getClass();
			if (Map.class.isAssignableFrom(clazz)) {
				ret = map.getClass().newInstance();
			 } else {
				ret = new LinkedHashMap<>();
			}
		} catch (IllegalAccessException | InstantiationException ignore) {
			ret =  new LinkedHashMap<>();
		}

		for (Entry<?, ?> entry : map.entrySet()) {
    		String k = ekm.apply(entry.getKey().toString());
    		Object v = entry.getValue();
    		if (v instanceof Map) {
    			v = cloneMap((Map<?,?>)v, ekm);
    		}
    		ret.put(k, v);
        }
    	return ret;
	}
	private Object createInstance(Class<?> instanceClass) {
		if (!instanceClass.isInterface()) {
			try {
				return instanceClass.getDeclaredConstructor().newInstance();
			} catch (NoSuchMethodException | InvocationTargetException | SecurityException | InstantiationException| IllegalAccessException | IllegalArgumentException  e) {
				LOG.error(e);
			}
		} else {
			if (Collection.class.isAssignableFrom(instanceClass)) {
				return new ArrayList<>();
			}
			if (Map.class.isAssignableFrom(instanceClass)) {
				return new HashMap<>();
			}
		}
		return null;
	}
	private Map<String, IDrain> getDrainMap(Class<?> contractType) {
		Map<String, IDrain> ret = new LinkedHashMap<>();
		Method[] methods = contractType.getDeclaredMethods();
		for (Method _t : methods) {
			IDrain drain = MethodDrain.getInstance(_t);
			if (drain != null && !ret.containsKey(drain.getName())) {
				ret.put(drain.getName(), drain);
			}
		}
		Field[] fields = contractType.getDeclaredFields();
		for (Field _t : fields) {
			IDrain drain = FieldDrain.getInstance(_t);
			if (drain != null && !ret.containsKey(drain.getName())) {
				ret.put(drain.getName(), drain);
			}
		}
		return ret;
	}
	private Object loopObj(Object projection, Class<?> projectionType, Map<String, Object> attributes, int depth) {
		if (attributes == null || depth < 0) return projection;
		if (projection == null) projection = createInstance(projectionType);
		if (projection == null) return null;
		Map<String, IDrain> drainMap = getDrainMap(projectionType);
		for (Entry<String, IDrain> ret : drainMap.entrySet()) {
			String attrName = ret.getKey();
			IDrain drain = ret.getValue();
			Class<?> attrType = drain.getType();
			if (attributes.containsKey(attrName)) {
				Object value = attributes.get(attrName);
				if (value instanceof Map) {
					Map<String, Object> nestedValue = cloneMap((Map<?,?>)value, keyMapper);
					try {
						drain.setValue(projection,
							attrType.isAssignableFrom(nestedValue.getClass()) 
								? nestedValue
								: loopObj(null, attrType, nestedValue, depth - 1));
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignore) {
						// no operation
					}
				} else {
					try {
						if (value instanceof List) {
							drain.setValue(projection, loopSeq(null, attrType, drain.getGenericType(), (List<?>)value, depth - 1));
						} else {
							drain.setValue(projection, typeMapper.changeType(value, attrType));
						}
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignore) {
						// no operation
					} 
				} 
			}
		}
		return projection;
	}
	
	
	private Object loopSeq(Object seq, Class<?> projectionType, Class<?> itemClass, List<?> list, int depth) {
		if (depth < 0) {
			return null;
		}
		if (projectionType.isArray()) {
			return loopSeqFromArray(seq, projectionType, list, depth);
		} else if (List.class.isAssignableFrom(projectionType)) {
			return loopSeqFromList(seq, projectionType, itemClass, list, depth);
		} else if (Stream.class.isAssignableFrom(projectionType)) {
			return loopSeqFromStream(seq, projectionType, itemClass, list, depth);
		}
		return null;
	}

	private Object loopSeqFromArray(Object seq, Class<?> projectionType, List<?> list,
			int depth) {
		Class<?> componentType = projectionType.getComponentType();
		int seqLen = seq == null ? 0 : Array.getLength(seq);
		Object projection = Array.newInstance(componentType, seqLen + list.size());
		int i = 0;
		for (; i < seqLen; i++) {
			Array.set(projection, i, Array.get(seq, i));
		}
		int arrayLen = Array.getLength(projection);
		Iterator<?> iter = list.iterator();
		for (; iter.hasNext() && i < arrayLen; i++) {
			Object item = iter.next();
			if (item == null) {
				if (!componentType.isPrimitive())
					Array.set(projection, i, null);
			} else {
				if (item instanceof Map) {
					Array.set(projection, i, 
						projectionType.isAssignableFrom(item.getClass())
							? item
							: loopObj(null, componentType, cloneMap((Map<?,?>)item, this.keyMapper), depth - 1));
					
				} else {
					Array.set(projection, i, 
						(item instanceof List)
							? loopSeq(null, componentType, null, (List<?>)item, depth - 1)
							: typeMapper.changeType(item, componentType));
				}
			}
		}
		return projection;
	}
	
	@SuppressWarnings("unchecked")
	private Object loopSeqFromList(Object seq, Class<?> projectionType, Class<?> itemClass, List<?> list, int depth) {
		@SuppressWarnings("rawtypes")
		List projection = (List<?>)createInstance(projectionType);
		if (projection == null) {
			return null;
		}
		if (seq instanceof List) {
			projection.addAll((List<?>)seq);
		}
		if (list != null) {
			if (itemClass == null) {
				for (Object item : list) { 
					if (item == null) {
						projection.add(null);
					} else {
						if (item instanceof Map) {
							Object o = loopObj(null, item.getClass(), cloneMap((Map<?,?>)item, this.keyMapper), depth - 1);
							projection.add(o);
						} else {
							projection.add((item instanceof List)
								? loopSeq(null, item.getClass(), null,(List<?>)item, depth - 1)
								: typeMapper.changeType(item, item.getClass()));
						}
					}
				}
			} else {
				for (Object item : list) { 
					if (item == null) {
						projection.add(null);
					} else {
						if (item instanceof Map) {
							Object o = itemClass.isAssignableFrom(Map.class)
								? item
								: loopObj(null, itemClass, cloneMap((Map<?,?>)item, this.keyMapper), depth - 1);
							projection.add(o);
						} else {
							projection.add((item instanceof List)
								? loopSeq(null, item.getClass(), itemClass, (List<?>)item, depth - 1)
								: typeMapper.changeType(item, itemClass));
						}
					}
				}
			}
		}
		return projection;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object loopSeqFromStream(Object seq, Class<?> projectionType, Class<?> itemClass, List<?> list, int depth) {
		
		Stream.Builder projection;
		if (Stream.class.isAssignableFrom(projectionType)) {
			projection = Stream.builder();
		} else {
			return null;
		}
		TypeVariable<?> genericType = projectionType.getTypeParameters()[0];
		Class<?> genericClass = genericType.getClass();
		if (seq instanceof List) {
			List seqList =  (List<?>)seq;
			for (Object item : seqList) {
				projection.add(typeMapper.changeType(item, genericClass));
			}
		}
		if (list != null) {
			if (itemClass == null) {
				for (Object item : list) { 
					if (item == null) {
						projection.add(null);
					} else {
						if (item instanceof Map) {
							Object o = loopObj(null, item.getClass(), cloneMap((Map<?,?>)item, this.keyMapper), depth - 1);
							projection.add(o);
						} else {
							projection.add((item instanceof List)
								? loopSeq(null, item.getClass(), null,(List<?>)item, depth - 1)
								: typeMapper.changeType(item, genericClass));
						}
					}
				}
			} else {
				for (Object item : list) { 
					if (item == null) {
						projection.add(null);
					} else {
						if (item instanceof Map) {
							Object o = itemClass.isAssignableFrom(Map.class)
								? item
								: loopObj(null, itemClass, cloneMap((Map<?,?>)item, this.keyMapper), depth - 1);
							projection.add(o);
						} else {
							projection.add((item instanceof List)
								? loopSeq(null, item.getClass(), itemClass, (List<?>)item, depth - 1)
								: typeMapper.changeType(item, itemClass));
						}
					}
				}
			}
		}
		return projection.build();
	}
}
