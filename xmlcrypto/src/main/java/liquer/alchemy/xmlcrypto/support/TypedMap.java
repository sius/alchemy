package liquer.alchemy.xmlcrypto.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

public class TypedMap extends YashMap {

    private static final Logger LOG = LoggerFactory.getLogger(TypedMap.class);

    private static final long serialVersionUID = 0L;

    public TypedMap() { super(); }
    public TypedMap(Map<String, Object> map) {
        super(map);
    }

    /**
     * Creates a new TypedMap instance that contains only (at maximum) the specified keys.
     * Not existing keys will be ignored.
     *
     * @param keys the Keys to preserve
     * @return the new pruned TypeMap instance
     */
	public TypedMap prune(String... keys) {
    	YashMap ret = new YashMap();
    	for (String key : keys) {
    		if (containsKey(key)) {
    		    ret.put(key, get(key));
            }
    	}
    	return new TypedMap(ret);
    }

    /**
     * Same as prune, but modifies the instance
     * @param keys the Keys to preserve
     * @return the same pruned TypeMap instance (this)
     */
	public TypedMap selfPrune(Object... keys) {
        Iterator<Map.Entry<String, Object>> iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> next = iter.next();
            boolean found = false;
            for (int i = 0; !found && i < keys.length; i++) {
                found = next.getKey().equals(keys[i]);
            }
            if (!found) {
                iter.remove();
            }
        }
        return this;
    }

    /**
     * Convenience method that calls put with the key.toString() value
     *
     * @param key the Object key
     * @param value the Object value
     */
    public void putWithObjectKey(Object key, Object value) {
        put(key == null ? null : key.toString(), value);
    }

    @Override
	public Object get(Object key) {
		return super.get(key == null ? null : key.toString());
	}

    public TypedMap valueOf(String key) { return valueOf(key, new YashMap()); }
	public TypedMap valueOf(String key, Map<String, Object> defaultValue) { return valueOf(this, key, defaultValue); }
    public List<TypedMap> listOf(String key) {
    	return listOf(this, key, new ArrayList<>());
    }

    /**
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public String deleteString(String key) { return deleteString(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public Long deleteLong(String key) { return deleteLong(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public Integer deleteInteger(String key) { return deleteInteger(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public Short deleteShort(String key) { return deleteShort(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public Double deleteDouble(String key) { return deleteDouble(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
    public Float deleteFloat(String key) { return deleteFloat(key, null); }

    /**
     *
     * @param key the specified key
     * @return the removed value or defaultValue if the specified key does not exist
     */
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
    public Optional<String> tryStringValue(String... keys) {
        return availableKeys(keys).map(this::stringValue).findFirst();
    }

    public <T extends Enum<T>> T enumValue(Class<T> clazz, String key) { return enumValue(clazz, key, null); }
    public <T extends Enum<T>> Optional<T> tryEnumValue(Class<T> clazz, String... keys) {
        return availableKeys(keys).map(s -> enumValue(clazz, s)).findFirst();
    }

    public Long longValue(String key) { return longValue(key, null); }
    public Optional<Long> tryLongValue(String... keys) {
        return availableKeys(keys).map(this::longValue).findFirst();
    }

    public Integer integerValue(String key) { return integerValue(key, null); }
    public Optional<Integer> tryIntegerValue(String... keys) {
        return availableKeys(keys).map(this::integerValue).findFirst();
    }

    public Short shortValue(String key) { return shortValue(key, null); }
    public Optional<Short> tryShortValue(String... keys) {
        return availableKeys(keys).map(this::shortValue).findFirst();
    }

    public Double doubleValue(String key) { return doubleValue(key, null); }
    public Optional<Double> tryDoubleValue(String... keys) {
        return availableKeys(keys).map(this::doubleValue).findFirst();
    }

    public Float floatValue(String key) { return floatValue(key, null); }
    public Optional<Float> tryFloatValue(String... keys) {
        return availableKeys(keys).map(this::floatValue).findFirst();
    }

    public Boolean booleanValue(String key) { return booleanValue(key, null); }
    public Optional<Boolean> tryBooleanValue(String... keys) {
        return availableKeys(keys).map(this::booleanValue).findFirst();
    }

    public Stream<String> availableKeys(String... keys) {
        return Arrays.asList(keys).stream().filter(s -> get(s) != null);
    }

    public String stringValue(String key, String defaultValue) { return stringValue(this, key, defaultValue); }
	public <T extends Enum<T>> T enumValue(Class<T> clazz, String key, T defaultValue) { return enumValue(clazz, this, key, defaultValue); }
    public Long longValue(String key, Long defaultValue) { return longValue(this, key, defaultValue); }
    public Integer integerValue(String key, Integer defaultValue) { return integerValue(this, key, defaultValue); }
    public Short shortValue(String key, Short defaultValue) { return shortValue(this, key, defaultValue); }
    public Double doubleValue(String key, Double defaultValue) { return doubleValue(this, key, defaultValue); }
    public Float floatValue(String key, Float defaultValue) { return floatValue(this, key, defaultValue); }
    public Boolean booleanValue(String key, Boolean defaultValue) { return booleanValue(this, key, defaultValue); }

    public String[] stringArray(String key) { return stringArray(key, null); }
    public Optional<String[]> tryStringArray(String... keys) {
        return availableKeys(keys).map(this::stringArray).findFirst();
    }
    public Long[] longArray(String key) { return longArray(key, null); }
    public Optional<Long[]> tryLongArray(String... keys) {
        return availableKeys(keys).map(this::longArray).findFirst();
    }

    public Integer[] integerArray(String key) { return integerArray(key, null); }
    public Optional<Integer[]> tryIntegerArray(String... keys) {
        return availableKeys(keys).map(this::integerArray).findFirst();
    }

    public Short[] shortArray(String key) { return shortArray(key, null); }
    public Optional<Short[]> tryShortArray(String... keys) {
        return availableKeys(keys).map(this::shortArray).findFirst();
    }

    public Double[] doubleArray(String key) { return doubleArray(key, null); }
    public Optional<Double[]> tryDoubleArray(String... keys) {
        return availableKeys(keys).map(this::doubleArray).findFirst();
    }

    public Float[] floatArray(String key) { return floatArray(key, null); }
    public Optional<Float[]> tryFloatArray(String... keys) {
        return availableKeys(keys).map(this::floatArray).findFirst();
    }

    public Boolean[] booleanArray(String key) { return booleanArray(key, null); }
    public Optional<Boolean[]> tryBooleanArray(String... keys) {
        return availableKeys(keys).map(this::booleanArray).findFirst();
    }

	public List<String> stringList(String key) { return Arrays.asList(stringArray(this, key, null)); }
    public Optional<List<String>> tryStringList(String... keys) {
        return availableKeys(keys).map(this::stringList).findFirst();
    }

	public List<Long> longList(String key) { return Arrays.asList(longArray(this, key, null)); }
    public Optional<List<Long>> tryLongList(String... keys) {
        return availableKeys(keys).map(this::longList).findFirst();
    }

	public List<Integer> integerList(String key) { return Arrays.asList(integerArray(this, key, null)); }
    public Optional<List<Integer>> tryIntegerList(String... keys) {
        return availableKeys(keys).map(this::integerList).findFirst();
    }

	public List<Short> shortList(String key) { return Arrays.asList(shortArray(this, key, null)); }
    public Optional<List<Short>> tryShortList(String... keys) {
        return availableKeys(keys).map(this::shortList).findFirst();
    }

	public List<Double> doubleList(String key) { return Arrays.asList(doubleArray(this, key, null)); }
    public Optional<List<Double>> tryDoubleList(String... keys) {
        return availableKeys(keys).map(this::doubleList).findFirst();
    }

	public List<Float> floatList(String key) { return Arrays.asList(floatArray(this, key, null)); }
    public Optional<List<Float>> tryFloatList(String... keys) {
        return availableKeys(keys).map(this::floatList).findFirst();
    }

	public List<Boolean> booleanList(String key) { return Arrays.asList(booleanArray(this, key, null)); }
    public Optional<List<Boolean>> tryBooleanList(String... keys) {
        return availableKeys(keys).map(this::booleanList).findFirst();
    }

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
    public <T extends Enum<T>> List<T> enumList(Class<T> clazz, String key, List<T> defaultValue) {
        T[] arr = enumArray(clazz, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Long> longList(String key, List<Long> defaultValue) {
        Long[] arr = longArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Integer> integerList(String key, List<Integer> defaultValue) {
        Integer[] arr = integerArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Short> shortList(String key, List<Short> defaultValue) {
        Short[] arr = shortArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Double> doubleList(String key, List<Double> defaultValue) {
        Double[] arr = doubleArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Float> floatList(String key, List<Float> defaultValue) {
        Float[] arr = floatArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }
    public List<Boolean> booleanList(String key, List<Boolean> defaultValue) {
        Boolean[] arr = booleanArray(this, key, null);
        return arr == null ? defaultValue : Arrays.asList(arr);
    }

    public static TypedMap valueOf(Map<?,?> params, String key, Map<String, Object> defaultValue) {
        TypedMap ret = new TypedMap(defaultValue);
        if (params != null && key != null && !key.isEmpty()) {
            Object o = params.get(key);
            if (o instanceof Map) {
                ret = new TypedMap(YashMap.of((Map<?,?>)o));
            }
        }
        return ret;
    }
    public static String stringValue(Map<?,?> params, String key, String defaultValue) {
        String ret = defaultValue;
        if (params != null && StringSupport.notNullOrEmpty(key)) {
            ret = StringSupport.stringify(params.get(key), defaultValue);
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
                LOG.error(ignore.getMessage(), ignore);
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
            } catch (NumberFormatException ignore) {
                LOG.error(ignore.getMessage(), ignore);
            }
        }
        return ret;
    }
    public static Integer integerValue(Map<?,?> params, String key, Integer defaultValue) {
        Integer ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).intValue();
            } catch (NumberFormatException ignore) {
                LOG.error(ignore.getMessage(), ignore);
            }
        }
        return ret;
    }
    public static Short shortValue(Map<?,?> params, String key, Short defaultValue) {
        Short ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).shortValue();
            } catch (NumberFormatException ignore) {
                LOG.error(ignore.getMessage(), ignore);
            }
        }
        return ret;
    }
    public static Double doubleValue(Map<?,?> params, String key, Double defaultValue) {
        Double ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s);
            } catch (NumberFormatException ignore) {
                LOG.error(ignore.getMessage(), ignore);
            }
        }
        return ret;
    }
    public static Float floatValue(Map<?,?> params, String key, Float defaultValue) {
        Float ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            try {
                ret = Double.valueOf(s).floatValue();
            } catch (NumberFormatException ignore) {
                LOG.error(ignore.getMessage(), ignore);
            }
        }
        return ret;
    }
    public static Boolean booleanValue(Map<?,?> params, String key, Boolean defaultValue) {
        Boolean ret = defaultValue;
        String s = stringValue(params, key, null);
        if (s != null) {
            ret = Boolean.valueOf(s);
        }
        return ret;
    }
    public static boolean isArrayAttribute(Map<?,?> params, String key) {
        boolean ret = false;
        if (params != null && key != null && !key.equals("")) {
            Object o = params.get(key);
            if (o != null) {
                ret = o.getClass().isArray();
            }
         }
        return ret;
    }
	public static List<TypedMap> listOf(Map<?,?> params, String key, List<TypedMap> defaultValue) {
		List<TypedMap> ret = (defaultValue == null)
            ? new ArrayList<>()
            : new ArrayList(defaultValue);

    	if (params != null && StringSupport.notNullOrEmpty(key)) {
            ret = copyMapToList(params.get(key));
    	}
    	return ret;
    }

    private static List<TypedMap> copyMapToList(Object o) {
        final List<TypedMap> ret = new ArrayList<>();
        if (o instanceof Map<?,?>) {
            Map<?,?> m = (Map<?,?>)o;
            for (int i = 0; i < m.size(); i++) {
                String index = String.valueOf(i);
                if (m.containsKey(index)) {
                    ret.add(i, valueOf(m, index, new TypedMap()));
                }
            }
        }
        return ret;
    }

    public static String[] stringArray(Map<?,?> params, String key, String[] defaultValue) {
        String[] ret = (defaultValue == null)
            ? null
            : defaultValue.clone();

        if (params == null || StringSupport.isNullOrEmpty(key)) {
            return ret;
        }

        Object o = params.get(key);
        if (o == null) {
            return ret;
        }

        if (Collection.class.isAssignableFrom(o.getClass())) {
            o = ((Collection)o).toArray();
        }

        if (o.getClass().isArray()) {
            int len = Array.getLength(o);
            ret = new String[len];
            for (int i = 0; i < len; i++) {
                Object v = Array.get(o, i);
                ret[i] = (v != null) ? v.toString() : null;
            }
        } else {
            ret = new String[] { o.toString() };
        }

        return ret;
    }

	public static <T extends Enum<T>> T[] enumArray(Class<T> componentType, Map<?,?> params, String key, T[] defaultValue) {
		T[] ret = (defaultValue == null) ? null : defaultValue.clone();
		String[] arr = stringArray(params, key, new String[0]);
		if (arr.length > 0) {
			int i = 0;
			T[] farr = (T[])Array.newInstance(componentType, arr.length);
			for (String s : arr) {
				try {
					Array.set(farr, i++, Enum.valueOf(componentType, s));
				} catch (IllegalArgumentException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
			}
			ret = farr;
		}
		return ret;
	}
    public static Long[] longArray(Map<?,?> params, String key, Long[] defaultValue) {
        Long[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Long[] farr = new Long[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Long.valueOf(s);
                } catch (NumberFormatException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
            }
            ret = farr;
        }
        return ret;
    }
    public static Integer[] integerArray(Map<?,?> params, String key, Integer[] defaultValue) {
        Integer[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Integer[] farr = new Integer[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Integer.valueOf(s);
                } catch (NumberFormatException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
            }
            ret = farr;
        }
        return ret;
    }
    public static Short[] shortArray(Map<?,?> params, String key, Short[] defaultValue) {
        Short[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Short[] farr = new Short[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Short.valueOf(s);
                } catch (NumberFormatException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
            }
            ret = farr;
        }
        return ret;
    }
    public static Double[] doubleArray(Map<?,?> params, String key, Double[] defaultValue) {
        Double[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Double[] farr = new Double[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Double.valueOf(s);
                } catch (NumberFormatException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
            }
            ret = farr;
        }
        return ret;
    }
    public static Float[] floatArray(Map<?,?> params, String key, Float[] defaultValue) {
        Float[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Float[] farr = new Float[strings.length];
            for (String s : strings) {
                try {
                    farr[i++] = Float.valueOf(s);
                } catch (NumberFormatException ignore) {
                    LOG.error(ignore.getMessage(), ignore);
                }
            }
            ret = farr;
        }
        return ret;
    }
    public static Boolean[] booleanArray(Map<?,?> params, String key, Boolean[] defaultValue) {
        Boolean[] ret = (defaultValue == null) ? null : defaultValue.clone();
        String[] strings = stringArray(params, key, new String[0]);
        if (strings.length > 0) {
            int i = 0;
            Boolean[] farr = new Boolean[strings.length];
            for (String s : strings) {
                farr[i++] = Boolean.valueOf(s);
            }
            ret = farr;
        }
        return ret;
    }
}
