package liquer.alchemy.athanor.reflect;

import liquer.alchemy.athanor.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

public final class TypeMapper {
	
	private static final Logger LOG = LogManager.getLogger(TypeMapper.class);
	
	private static final Map<Class<?>, Object> DEFAULT_INSTANCES = new HashMap<>();
	private static final Map<Class<?>, Class<?>> INTERFACES_IMPL_MAP = new HashMap<>();
	
	static {
		DEFAULT_INSTANCES.put(Boolean.class, Boolean.FALSE);
		DEFAULT_INSTANCES.put(Byte.class, (byte)0);
		DEFAULT_INSTANCES.put(Character.class, '\0');
		DEFAULT_INSTANCES.put(Double.class,0d);
		DEFAULT_INSTANCES.put(Float.class, 0f);
		DEFAULT_INSTANCES.put(Integer.class,0);
		DEFAULT_INSTANCES.put(Long.class,0L);
		DEFAULT_INSTANCES.put(Short.class, (short)0);

		INTERFACES_IMPL_MAP.put(List.class, ArrayList.class);
		INTERFACES_IMPL_MAP.put(Map.class, LinkedHashMap.class);
	}

	@SuppressWarnings("unchecked")
	public static <P> P newInstance(Class<P> projection) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if (projection.isPrimitive()) {
			return (P) Object.class.getDeclaredConstructor().newInstance();
		} else if (projection.isInterface()) {
			return (P) TypeMapper.implType(projection).getDeclaredConstructor().newInstance();
		} else if (DEFAULT_INSTANCES.containsKey(projection)){
			return (P) DEFAULT_INSTANCES.get(projection);
		} else {
			return projection.getDeclaredConstructor().newInstance();
		}
	}
	
	public static Class<?> implType(Class<?> interfaceType) {
		if (interfaceType == null) {
			return Object.class;
		}
		if (interfaceType.isInterface()) {
			if (INTERFACES_IMPL_MAP.containsKey(interfaceType)) {
				return INTERFACES_IMPL_MAP.get(interfaceType);
			}
			return Object.class;
		}
		return interfaceType;
		
	}

	private Map<String, Method> typeMap = new HashMap<>();
	public TypeMapper() {
		Method[] methods = getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getParameterTypes().length == 1) {
				typeMap.put(method.getParameterTypes()[0].getName() + "_" + method.getReturnType().getName(), method);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> T changeType(Object from, Class<T> to) {
		if (from == null) return null;
		if (to.isAssignableFrom(from.getClass())) return to.cast(from);
		String mapperId = from.getClass().getName() + "_" + to.getName();
		Method mapper = typeMap.get(mapperId);
		if (mapper == null) return null;
		try {
			if (to.isPrimitive()) {
				return (T)mapper.invoke(to, from);
			}
			return to.cast(mapper.invoke(to, from));
		} catch (Exception e) { 
			LOG.error(e);
		}
		return null;
	}
	
	public static boolean booleanToPrimitiveBoolean(Boolean value) { return value; }
	public static int booleanToInt(Boolean value) { return value ? 1 : 0; }
	public static Integer booleanToInteger(Boolean value) { return value ? 1 : 0; }
	public static long booleanToPrimitiveLong(Boolean value) { return value ? 1L : 0L; }
	public static Long booleanToLong(Boolean value) { return value ? Long.valueOf(1) : Long.valueOf(0); }
	public static byte doubleToPrimitiveByte(Double value) { return value.byteValue(); }
	public static Byte doubleToByte(Double value) { return value.byteValue(); }
	public static short doubleToPrimitiveShort(Double value) { return value.shortValue(); }
	public static Short doubleToShort(Double value) { return value.shortValue(); }
	public static int doubleToInt(Double value) { return value.intValue();}
	public static Integer doubleToInteger(Double value) { return value.intValue();}
	public static long doubleToPrimitiveLong(Double value) { return value.longValue(); }
	public static Long doubleToLong(Double value) { return value.longValue(); }
	public static float doubleToPrimitiveFloat(Double value) { return value.floatValue(); }
	public static Float doubleToFloat(Double value) { return value.floatValue();}
	public static double doubleToPrimitiveDouble(Double value) { return value; }
	public static BigDecimal doubleToBigDecimal(Double value) { return BigDecimal.valueOf(value);}
	public static BigInteger doubleToBigInteger(Double value) { return BigInteger.valueOf(value.longValue());}
	public static String doubleToString(Double value) { return value.toString();}
	public static Date stringToDate(String value) throws ParseException { 
		try {
			return Json.Singleton.CONFIG.getSimpleDateFormat().parse(value);
		} catch (ParseException ignore) {
			return null; //Json.Singleton.CONFIG.getDefaultSimpleDateFormat().parse(value);
		}
	}
	public static Long stringToLong(String value) { return Long.valueOf(value);}
	public static long stringToPrimitiveLong(String value) { return Long.valueOf(value);}
	public static BigDecimal stringToBigDecimal(String value) { return new BigDecimal(value);}
	public static BigInteger stringToBigInteger(String value) { return new BigInteger(value);}
}
