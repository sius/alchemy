package liquer.alchemy.xmlcrypto.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Yet another hash map based on the <code>LinkedHashMap</code>.
 */
public class YashMap extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 0L;

    public YashMap() {
        super();
    }

    public YashMap(int capacity) {
        super(capacity);
    }

    public YashMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);

    }

    protected YashMap(Map<String, Object> map) {
        super(map == null ? new LinkedHashMap<>() : new LinkedHashMap<>(map));
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

        final YashMap ret = new YashMap();
        for (int i = 0; i < keyValuePairs.length; i++) {
            String key = keyValuePairs[i].toString();
            if (ret.containsKey(key)) {
                Object value = ret.get(keyValuePairs[i]);
                List<Object> arr = copyValueToList(value);
                int next = i + 1;
                arr.add((keyValuePairs.length > next ? keyValuePairs[next] : null));
                ret.put(key, arr.toArray());
                i++;
            } else {
                ret.put(key, (keyValuePairs.length > (++i) ? keyValuePairs[i] : null));
            }
        }
        return ret;
    }

    private static List<Object> copyValueToList(Object value) {
        final List<Object> ret = new ArrayList<>();
        if (value.getClass().isArray()) {
            final int len = Array.getLength(value);
            for (int l = 0; l < len; l++) {
                ret.add(Array.get(value, l));
            }
        } else {
            ret.add(value);
        }
        return ret;
    }

    public YashMap dup() {
        return new YashMap(this);
    }

    @Override
    public Object put(String key, Object value) {

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

    public void replace(Map<String, Object> that) {
        if (that != null) {
            clear();
            for (Map.Entry<String, Object> entry : that.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Merges 2 Maps overriding existing key values with the key values of that.
     * Adds all new entries from that.
     *
     * @param that the entry supplier Map
     * @return a new YashMap instance containing the merged entries
     */
    public YashMap merge(Map<String, Object> that) {
        YashMap ret = new YashMap(this);
        if (that != null) {
            for (Map.Entry<String, Object> entry : that.entrySet()) {
                ret.put(entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    /**
     * Merges 2 Maps overriding existing key values with the key values of that.
     * Adds all new entries from that to the instance.
     *
     * @param that the entry supplier Map
     */
    public void selfMerge(Map<String, Object> that) {
        if (that != null) {
            for (Map.Entry<String, Object> entry : that.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public YashMap reverseMerge(Map<String, Object> that) {
        if (that != null) {
            final YashMap thatMap = (that instanceof YashMap)
                ? (YashMap)that
                : new YashMap(that);

            return thatMap.merge(this);
        }
        return new YashMap(this);
    }

    public void selfReverseMerge(Map<String, Object> that) {
        if (that != null) {
            final YashMap thatMap = (that instanceof YashMap)
                ? (YashMap)that
                : new YashMap(that);

            replace(thatMap.merge(this));
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
        Object ret = defaultValue;
        if (key != null) {
            ret = remove(key);
        }
        return ret;
    }

    public YashMap stringifyValues() {
        YashMap ret = new YashMap();
        for (Map.Entry<String, Object> next : entrySet()) {
            ret.put(next.getKey(), String.valueOf(next.getValue()));
        }
        return ret;
    }

    public void selfStringifyValues() {
        YashMap ret = new YashMap();
        for (Map.Entry<String, Object> next : this.entrySet()) {
            ret.put(next.getKey(), (next.getValue() == null ? "" : next.getValue().toString()));
        }
        clear();
        selfMerge(ret);
    }
}
