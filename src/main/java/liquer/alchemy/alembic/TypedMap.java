package liquer.alchemy.alembic;

import java.lang.reflect.Array;
import java.util.*;


@SuppressWarnings("unchecked")
public class TypedMap extends Yash {

    public TypedMap() { }
    public TypedMap(Map<String, Object> map) {
        super(map);
    }

	public TypedMap require(String... keys) {
    	Yash ret = new Yash();
    	for (String key : keys) {
    		if (containsKey(key))
    			ret.put(key, get(key));
    	}
    	return new TypedMap(ret);
    }
	public TypedMap selfRequire(Object... keys) {
        Iterator<Map.Entry<String, Object>> iter = this.entrySet().iterator();
        for (;iter.hasNext();) {
            Map.Entry<String, Object> next = iter.next();
            boolean found = false;
            for (int i = 0; !found && i < keys.length; i++)
            	found = next.getKey().equals(keys[i]);
            if (!found) iter.remove();
        }
        return this;
    }
    public void putWithObjectKey(Object key, Object value) {
        put(key == null ? null : key.toString(), value);
    }
    @Override
	public Object get(Object key) {
		return super.get(key == null ? null : key.toString());
	}

    public TypedMap valueOf(String key) { return valueOf(key, new Yash()); }
	public TypedMap valueOf(String key, Map<String, Object> defaultValue) { return valueOf(this, key, defaultValue); }
    public List<TypedMap> listOf(String key) {
    	return listOf(this, key, new ArrayList<>());
    }
    public String deleteString(String key) { return deleteString(key, null); }
    public Long deleteLong(String key) { return deleteLong(key, null); }
    public Integer deleteInteger(String key) { return deleteInteger(key, null); }
    public Short deleteShort(String key) { return deleteShort(key, null); }
    public Double deleteDouble(String key) { return deleteDouble(key, null); }
    public Float deleteFloat(String key) { return deleteFloat(key, null); }
    public Boolean deleteBoolean(String key) { return deleteBoolean(key, null); }

    public String[] deleteStrings(String key) { return deleteStrings(key, null); }
    public Long[] deleteLongs(String key) { return deleteLongs(key, null); }
    public Integer[] deleteIntegers(String key) { return deleteIntegers(key, null); }
    public Short[] deleteShorts(String key) { return deleteShorts(key, null); }
    public Double[] deleteDoubles(String key) { return deleteDoubles(key, null); }
    public Float[] deleteFloats(String key) { return deleteFloats(key, null); }
    public Boolean[] deleteBooleans(String key) { return deleteBooleans(key, null); }


    public String deleteString(String key, String defaultValue) {
    	String ret = stringValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Long deleteLong(String key, Long defaultValue) {
    	Long ret = longValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Integer deleteInteger(String key, Integer defaultValue) {
    	Integer ret = integerValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Short deleteShort(String key, Short defaultValue) {
    	Short ret = shortValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Double deleteDouble(String key, Double defaultValue) {
    	Double ret = doubleValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Float deleteFloat(String key, Float defaultValue) {
    	Float ret = floatValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }
    public Boolean deleteBoolean(String key, Boolean defaultValue) {
    	Boolean ret = booleanValue(this, key, defaultValue);
    	remove(key);
        return ret;
    }

    public String[] deleteStrings(String key, String[] defaultValue) {
    	String[] ret = stringArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Long[] deleteLongs(String key, Long[] defaultValue) {
    	Long[] ret = longArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Integer[] deleteIntegers(String key, Integer[] defaultValue) {
    	Integer[] ret = integerArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Short[] deleteShorts(String key, Short[] defaultValue) {
    	Short[] ret =  shortArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Double[] deleteDoubles(String key, Double[] defaultValue) {
    	Double[] ret = doubleArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Float[] deleteFloats(String key, Float[] defaultValue) {
    	Float[] ret = floatArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}
    public Boolean[] deleteBooleans(String key, Boolean[] defaultValue) {
    	Boolean[] ret = booleanArray(this, key, defaultValue);
    	remove(key);
        return ret;
	}

    public String stringValue(String key) { return stringValue(key, null); }
	public <T extends Enum<T>> T enumValue(Class<T> clazz, String key) { return enumValue(clazz, key, null); }
    public Long longValue(String key) { return longValue(key, null); }
    public Integer integerValue(String key) { return integerValue(key, null); }
    public Short shortValue(String key) { return shortValue(key, null); }
    public Double doubleValue(String key) { return doubleValue(key, null); }
    public Float floatValue(String key) { return floatValue(key, null); }
    public Boolean booleanValue(String key) { return booleanValue(key, null); }

    public String stringValue(String key, String defaultValue) { return stringValue(this, key, defaultValue); }
	public <T extends Enum<T>> T enumValue(Class<T> clazz, String key, Enum<T> defaultValue) { return enumValue(clazz, this, key, null); }
    public Long longValue(String key, Long defaultValue) { return longValue(this, key, defaultValue); }
    public Integer integerValue(String key, Integer defaultValue) { return integerValue(this, key, defaultValue); }
    public Short shortValue(String key, Short defaultValue) { return shortValue(this, key, defaultValue); }
    public Double doubleValue(String key, Double defaultValue) { return doubleValue(this, key, defaultValue); }
    public Float floatValue(String key, Float defaultValue) { return floatValue(this, key, defaultValue); }
    public Boolean booleanValue(String key, Boolean defaultValue) { return booleanValue(this, key, defaultValue); }

    public String[] stringArray(String key) { return stringArray(key, null); }
    public Long[] longArray(String key) { return longArray(key, null); }
    public Integer[] integerArray(String key) { return integerArray(key, null); }
    public Short[] shortArray(String key) { return shortArray(key, null); }
    public Double[] doubleArray(String key) { return doubleArray(key, null); }
    public Float[] floatArray(String key) { return floatArray(key, null); }
    public Boolean[] booleanArray(String key) { return booleanArray(key, null); }

	public List<String> stringList(String key) { return Arrays.asList(stringArray(this, key, null)); }
	public List<Long> longList(String key) { return Arrays.asList(longArray(this, key, null)); }
	public List<Integer> integerList(String key) { return Arrays.asList(integerArray(this, key, null)); }
	public List<Short> shortList(String key) { return Arrays.asList(shortArray(this, key, null)); }
	public List<Double> doubleList(String key) { return Arrays.asList(doubleArray(this, key, null)); }
	public List<Float> floatList(String key) { return Arrays.asList(floatArray(this, key, null)); }
	public List<Boolean> booleanList(String key) { return Arrays.asList(booleanArray(this, key, null)); }

	public String[] stringArray(String key, String[] defaultValue) { return stringArray(this, key, defaultValue); }
	public <T extends Enum<T>> T[] enumArray(Class<T> clazz, String key, T[] defaultValue) { return enumArray(clazz, this, key, null); }
	public Long[] longArray(String key, Long[] defaultValue) { return longArray(this, key, defaultValue); }
    public Integer[] integerArray(String key, Integer[] defaultValue) { return integerArray(this, key, defaultValue); }
    public Short[] shortArray(String key, Short[] defaultValue) { return shortArray(this, key, defaultValue); }
    public Double[] doubleArray(String key, Double[] defaultValue) { return doubleArray(this, key, defaultValue); }
    public Float[] floatArray(String key, Float[] defaultValue) { return floatArray(this, key, defaultValue); }
    public Boolean[] booleanArray(String key, Boolean[] defaultValue) { return booleanArray(this, key, defaultValue); }

	public List<TypedMap> listOf(String key, List<TypedMap> defaultValue) { return listOf(this, key, defaultValue); }

	public List<String> stringList(String key, String[] defaultValue) { return Arrays.asList(stringArray(this, key, defaultValue)); }
	public <T extends Enum<T>> List<T> enumList(Class<T> clazz, String key, T[] defaultValue) { return Arrays.asList(enumArray(clazz, this, key, null)); }
	public List<Long> longList(String key, Long[] defaultValue) { return Arrays.asList(longArray(this, key, defaultValue)); }
	public List<Integer> integerList(String key, Integer[] defaultValue) { return Arrays.asList(integerArray(this, key, defaultValue)); }
	public List<Short> shortList(String key, Short[] defaultValue) { return Arrays.asList(shortArray(this, key, defaultValue)); }
	public List<Double> doubleList(String key, Double[] defaultValue) { return Arrays.asList(doubleArray(this, key, defaultValue)); }
	public List<Float> floatList(String key, Float[] defaultValue) { return Arrays.asList(floatArray(this, key, defaultValue)); }
	public List<Boolean> booleanList(String key, Boolean[] defaultValue) { return Arrays.asList(booleanArray(this, key, defaultValue)); }

	public static TypedMap valueOf(Map<?,?> params, String key, Map<String, Object> defaultValue) {
    	TypedMap ret = new TypedMap(defaultValue);
    	if (params != null && key != null && !key.equals("")) {
	    	Object o = params.get(key);
	    	if (o instanceof Map)
	    		ret = new TypedMap(Yash.of((Map<?,?>)o));
    	}
    	return ret;
    }
    public static String stringValue(Map<?,?> params, String key, String defaultValue) {
        String ret = defaultValue;
        if (params != null && key != null && !key.equals("")) {
            Object o = params.get(key);
            if (o != null && o.getClass().isArray()) {
                int len = Array.getLength(o);
                if (len > 0) {
                    Object v = Array.get(o, 0);
                    ret = (v != null ? ret : "");
                } else {
                    ret = "";
                }
            } else {
                if (o != null)
                    ret = o.toString();
            }
        }
        return ret;
    }
    public static <T extends Enum<T>> T enumValue(Class<T> clazz, Map<?,?> params, String key, T defaultValue) {
	    T ret = defaultValue;
	    String s = stringValue(params, key, null);
	    if (s != null) {
	    	try {
			    ret = Enum.valueOf(clazz, s);
		    } catch (IllegalArgumentException ignore) {

		    }
	    }
	    return ret;
    }
    public static Long longValue(Map<?,?> params, String key, Long defaultValue) {
        Long ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).longValue();
            } catch (NumberFormatException ignore) {}
        }
        return ret;
    }
    public static Integer integerValue(Map<?,?> params, String key, Integer defaultValue) {
        Integer ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).intValue();
            } catch (NumberFormatException ignore) { }
        }
        return ret;
    }
    public static Short shortValue(Map<?,?> params, String key, Short defaultValue) {
        Short ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).shortValue();
            } catch (NumberFormatException ignore) {}
        }
        return ret;
    }
    public static Double doubleValue(Map<?,?> params, String key, Double defaultValue) {
        Double ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s);
            } catch (NumberFormatException ignore) {}
        }
        return ret;
    }
    public static Float floatValue(Map<?,?> params, String key, Float defaultValue) {
        Float ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).floatValue();
            } catch (NumberFormatException ignore) {}
        }
        return ret;
    }
    public static Boolean booleanValue(Map<?,?> params, String key, Boolean defaultValue) {
        Boolean ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null)
            ret = Boolean.valueOf(s);
        return ret;
    }
    public static boolean isArrayAttribute(Map<?,?> params, String key) {
        boolean ret = false;
        if (params != null && key != null && !key.equals("")) {
            Object o = params.get(key);
            if (o != null)
                ret = o.getClass().isArray();
         }
        return ret;
    }
	public static List<TypedMap> listOf(Map<?,?> params, String key, List<TypedMap> defaultValue) {
		List<TypedMap> ret = defaultValue;
    	if (params != null && key != null && !key.equals("")) {
	    	Object o = params.get(key);
	    	if (o instanceof Map<?,?>) {
	    		Map<?,?> m = (Map<?,?>)o;
	    		ret = new ArrayList<>();
	    		for (int i = 0; i < m.size(); i++) {
	    			String index = String.valueOf(i);
	    			if (m.containsKey(index))
	    				ret.add(i, valueOf(m, index, new TypedMap()));
	    		}
            }
    	}
    	return ret;
    }

    public static String[] stringArray(Map<?,?> params, String key, String[] defaultValue) {
        String[] ret = defaultValue;
        if (params != null && key != null && !key.equals("")) {
            Object o = params.get(key);
            if (o != null) {
                if (o.getClass().isArray()) {
                    int len = Array.getLength(o);
                    ret = new String[len];
                    for (int i = 0; i < len; i++) {
                        Object v = Array.get(o, i);
                        if (v != null)
                            ret[i] = v.toString();
                        else
                            ret[i] = null;
                    }
                } else {
                    ret = new String[] { o.toString() };
                }
            }
        }
        return ret;
    }

	public static <T extends Enum<T>> T[] enumArray(Class<T> clazz, Map<?,?> params, String key, T[] defaultValue) {
		T[] ret = defaultValue;
		String[] strings = stringArray(params, key, new String[0]);
		if (strings.length > 0) {
			int i = 0;
			T[] farr = (T[])Array.newInstance(clazz, strings.length);
			for (String s : strings) {
				try {
					Array.set(farr, i++, Enum.valueOf(clazz, s));
				} catch (IllegalArgumentException ignore) { }
			}
			ret = farr;
		}
		return ret;
	}
    public static Long[] longArray(Map<?,?> params, String key, Long[] defaultValue) {
        Long[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Long[] farr = new Long[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Long.valueOf(s);
                } catch (NumberFormatException ignore) { }
            }
            ret = farr;
        }
        return ret;
    }
    public static Integer[] integerArray(Map<?,?> params, String key, Integer[] defaultValue) {
        Integer[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Integer[] farr = new Integer[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Integer.valueOf(s);
                } catch (NumberFormatException ignore) { }
            }
            ret = farr;
        }
        return ret;
    }
    public static Short[] shortArray(Map<?,?> params, String key, Short[] defaultValue) {
        Short[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Short[] farr = new Short[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Short.valueOf(s);
                } catch (NumberFormatException ignore) { }
            }
            ret = farr;
        }
        return ret;
    }
    public static Double[] doubleArray(Map<?,?> params, String key, Double[] defaultValue) {
        Double[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Double[] farr = new Double[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Double.valueOf(s);
                } catch (NumberFormatException ignore) { }
            }
            ret = farr;
        }
        return ret;
    }
    public static Float[] floatArray(Map<?,?> params, String key, Float[] defaultValue) {
        Float[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Float[] farr = new Float[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Float.valueOf(s);
                } catch (NumberFormatException ignore) { }
            }
            ret = farr;
        }
        return ret;
    }
    public static Boolean[] booleanArray(Map<?,?> params, String key, Boolean[] defaultValue) {
        Boolean[] ret = defaultValue;
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Boolean[] farr = new Boolean[strings.length];
            for (String s : strings)
                farr[i++] = Boolean.valueOf(s);
            ret = farr;
        }
        return ret;
    }
}
