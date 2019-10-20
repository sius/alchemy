package liquer.alchemy.xmlcrypto.support;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Yet another hash map based on the <code>LinkedHashMap</code>.
 */
public class YashMap extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 4802126420623311422L;

    public static YashMap unmodifiable(YashMap other) {
        return new YashMap(other, true);
    }

    public static YashMap urlDecode(String queryString) throws IOException {
        return NestedParamsSupport.parseNestedQuery(queryString, "&");
    }

    public static String urlEncode(Object o, String prefix) throws IllegalArgumentException, UnsupportedEncodingException {
        return NestedParamsSupport.buildNestedQuery(o, prefix);
    }

    public static YashMap of(Map<?, ?> map) {
        if (map instanceof YashMap) {
            return ((YashMap) map).dup();
        }
        YashMap ret = new YashMap();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String k = entry.getKey().toString();
            Object v = entry.getValue();
            if (v instanceof Map) {
                v = of((Map<?, ?>) v);
            }
            ret.put(k, v);
        }
        return ret;
    }

    /**
     * Creates a new HashMap with Entries build from the consecutive pairs.
     * Adding an existing Key changes the referenced Value to an Array.
     * Java 9 Style Map.of
     *
     * @param keyValuePairs key and value alternating
     */
    public static YashMap of(Object... keyValuePairs) {
        return new YashMap(keyValuePairs);
    }

    private final boolean unmodifiable;

    public YashMap() {
        unmodifiable = false;
    }

    public YashMap(int capacity) {
        super(capacity);
        unmodifiable = false;
    }

    public YashMap(Map<String, Object> map) {
        this(map, false);
    }

    public YashMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);
        unmodifiable = false;
    }

    protected YashMap(Map<String, Object> map, boolean unmodifiable) {
        super(map == null ? new LinkedHashMap<>() : new LinkedHashMap<>(map));
        this.unmodifiable = unmodifiable;
    }

    private YashMap(Object... keyValuePairs) {
        unmodifiable = false;
        for (int i = 0; i < keyValuePairs.length; i++) {
            String key = keyValuePairs[i].toString();
            if (containsKey(key)) {
                Object value = get(keyValuePairs[i]);
                List<Object> arr = new ArrayList<>();
                if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    for (int l = 0; l < len; l++) {
                        arr.add(Array.get(value, l));
                    }
                } else {
                    arr.add(value);
                }
                int next = i + 1;
                arr.add((keyValuePairs.length > next ? keyValuePairs[next] : null));
                this.put(key, arr.toArray());
                i++;
            } else {
                this.put(key, (keyValuePairs.length > (++i) ? keyValuePairs[i] : null));
            }
        }
    }

    public YashMap dup() {
        return new YashMap(this);
    }

    @Override
    public Object remove(Object o) {
        if (unmodifiable) {
            return null;
        }
        return super.remove(o);
    }

    @Override
    public void clear() {
        if (!unmodifiable) {
            super.clear();
        }
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (!unmodifiable) {
            super.putAll(map);
        }
    }

    @Override
    public Object putIfAbsent(String s, Object o) {
        if (!unmodifiable) {
            return super.putIfAbsent(s, o);
        }
        return null;
    }

    @Override
    public boolean replace(String s, Object o, Object v1) {
        if (unmodifiable) {
            return false;
        }
        return super.replace(s, o, v1);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Object> entry) {
        if (unmodifiable) {
            return false;
        }
        return super.removeEldestEntry(entry);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> biFunction) {
        if (!unmodifiable) {
            super.replaceAll(biFunction);
        }

    }

    @Override
    public Object put(String key, Object value) {
        if (unmodifiable) {
            return null;
        }
        if (value == null) {
            return super.put(key, null);
        } else {
            if (value instanceof Map) {
                return super.put(key, value);
            } else if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                Class<?> target = value.getClass();
                Object arr = Array.newInstance(target.getComponentType(), len);
                for (int i = 0; i < len; i++) {
                    Object o = Array.get(value, i);
                    Array.set(arr, i, o);
                }
                return super.put(key, arr);
            } else {
                return super.put(key, value);
            }
        }
    }

    public Map<Object, String> invert() {
        Map<Object, String> ret = new LinkedHashMap<>();
        for (Map.Entry<String, Object> next : entrySet()) {
            if (next.getValue() != null) {
                ret.put(next.getValue(), next.getKey());
            }
        }
        return ret;
    }

    public void replace(YashMap that) {
        if (unmodifiable) {
            return;
        }
        if (that != null) {
            clear();
            for (Map.Entry<String, Object> entry : that.entrySet())
                put(entry.getKey(), entry.getValue());
        }
    }

    public YashMap merge(YashMap that) {
        YashMap ret = new YashMap(this);
        if (that != null) {
            for (Map.Entry<String, Object> entry : that.entrySet())
                ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    public void selfMerge(YashMap that) {
        if (unmodifiable) {
            return;
        }
        if (that != null) {
            for (Map.Entry<String, Object> entry : that.entrySet())
                put(entry.getKey(), entry.getValue());
        }
    }

    public YashMap reverseMerge(YashMap that) {
        if (that != null) {
            return that.merge(this);
        }
        return new YashMap(this);
    }

    public void selfReverseMerge(YashMap that) {
        if (unmodifiable) {
            return;
        }
        if (that != null) {
            replace(that.merge(this));
        }
    }

    /**
     * @param key key
     * @return the deleted value
     */
    public Object delete(String key) {
        return delete(key, null);
    }

    public Object delete(String key, Object defaultValue) {
        if (unmodifiable) {
            return null;
        }
        Object ret = defaultValue;
        if (key != null) {
            ret = remove(key);
        }
        return ret;
    }

    /**
     * @param selector function
     * @return a new {@link YashMap} with the Entries selected by the BiFunction
     */
    public YashMap select(BiFunction<String, Object, Boolean> selector) {
        YashMap ret = new YashMap(this);
        if (selector != null) {
            for (Map.Entry<String, Object> next : entrySet()) {
                if (selector.apply(next.getKey(), next.getValue()))
                    ret.put(next.getKey(), next.getValue());
            }
        }
        return ret;
    }

    /**
     * @param selector function
     * @return a new {@link YashMap} without the Entries selected by the BiFunction
     */
    public YashMap reject(BiFunction<String, Object, Boolean> selector) {
        YashMap ret = new YashMap(this);
        if (selector != null) {
            for (Map.Entry<String, Object> next : entrySet()) {
                if (selector.apply(next.getKey(), next.getValue()))
                    ret.delete(next.getKey());
            }
        }
        return ret;
    }

    public YashMap each(BiFunction<String, Object, Void> b) {
        if (b != null) {
            for (Map.Entry<String, Object> next : entrySet()) {
                b.apply(next.getKey(), next.getValue());
            }
        }
        return this;
    }

    /**
     * @return a flattened Key Value Array
     */
    public Object[] toArray() {
        List<Object> list = new ArrayList<>();

        for (Map.Entry<String, Object> next : entrySet()) {
            String k = next.getKey();
            Object v = next.getValue();
            if (v == null) {
                list.add(new Object[]{k, null});
            } else {
                if (v instanceof YashMap) {
                    list.add(new Object[]{k, ((YashMap) v).toArray()});
                } else if (v instanceof Map) {
                    list.add(new Object[]{k, of((Map<?, ?>) v).toArray()});
                } else {
                    list.add(new Object[]{k, v});
                }
            }
        }
        return list.toArray();
    }

    public YashMap stringifyValues() {
        YashMap ret = new YashMap();
        for (Map.Entry<String, Object> next : entrySet()) {
            ret.put(next.getKey(), String.valueOf(next.getValue()));
        }
        return ret;
    }

    public void selfStringifyValues() {
        if (unmodifiable)
            return;
        YashMap ret = new YashMap();
        for (Map.Entry<String, Object> next : this.entrySet()) {
            ret.put(next.getKey(), (next.getValue() == null ? "" : next.getValue().toString()));
        }
        clear();
        selfMerge(ret);
    }

    public String urlEncode() throws IllegalArgumentException, UnsupportedEncodingException {
        return urlEncode("");
    }

    public String urlEncode(String prefix) throws IllegalArgumentException, UnsupportedEncodingException {
        return YashMap.urlEncode(this, prefix);
    }
}
