package liquer.alchemy.collection;

import liquer.alchemy.json.Json;
import liquer.alchemy.util.RackUtils;
import liquer.alchemy.util.Tuple.Tuple2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Yet another hash based on the <code>LinkedHashMap</code>.
 */
public class Yash extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 4802126420623311422L;


	public static Yash unmodifiable(Yash other) {
		Yash ret = other.dup();
		ret.unmodifiable = true;
		return ret;
	}
	public static Yash urlDecode(String queryString) throws IOException {
		return RackUtils.parseNestedQuery(queryString, "&");
	}

	public static String urlEncode(Object o, String prefix) throws IllegalArgumentException, UnsupportedEncodingException {
		return RackUtils.buildNestedQuery(o, prefix);
	}

	public static Yash of(Map<?,?> map) {
		if (map instanceof Yash) {
			return ((Yash)map).dup();
		}
		Yash ret = new Yash();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String k = entry.getKey().toString();
			Object v = entry.getValue();
			if (v instanceof Map) {
				v = of((Map<?,?>)v);
			}
			ret.put(k, v);
		}
		return ret;
	}

	/**
	 * Creates a new HashMap with Entries build from the consecutive pairs.
	 * Adding an existing Key changes the referenced Value to an Array.
	 * @param keyValuePairs key and value alternating
	 */
	public static Yash of(Object... keyValuePairs) {
		return new Yash(keyValuePairs);
	}
	private boolean unmodifiable;
	
	public Yash() { }
	public Yash(int capacity) {
		super(capacity);
	}
	public Yash(Map<String, Object> map) {
		super(map == null ? new LinkedHashMap<>() : new LinkedHashMap<>(map));
	}
	public Yash(int capacity, float loadFactor) {
	    super(capacity, loadFactor);
	}

  private Yash(Object... keyValuePairs) {
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
  public Yash dup() {
    return new Yash(this);
  }

	@Override
  public Object put(String key, Object value) {
	if (unmodifiable)
		return null;
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

  /* RoR Goodies */
  public Tuple2<String, Object> assoc(String key) {
    return (containsKey(key)) ? new Tuple2<>(key, get(key)) : null;
  }

  public Map<Object, String> invert() {
    Map<Object, String> ret = new LinkedHashMap<>();
      for (Map.Entry<String, Object> next : entrySet()) {
          if (next.getValue() != null)
              ret.put(next.getValue(), next.getKey());
      }
      return ret;
  }
  public void replace(Yash that) {
    if (unmodifiable)
			return;
    if (that != null) {
        clear();
        for (Map.Entry<String, Object> entry : that.entrySet())
            put(entry.getKey(), entry.getValue());
    }
  }
  public Yash merge(Yash that) {
      Yash ret = new Yash(this);
      if (that != null) {
          for (Map.Entry<String, Object> entry : that.entrySet())
              ret.put(entry.getKey(), entry.getValue());
      }
      return ret;
  }
  public void selfMerge(Yash that) {
    if (unmodifiable)
			return;
    if (that != null) {
        for (Map.Entry<String, Object> entry : that.entrySet())
            put(entry.getKey(), entry.getValue());
    }
  }
  public Yash reverseMerge(Yash that) {
      if (that != null)
          return that.merge(this);
      return new Yash(this);
  }
  public void selfReverseMerge(Yash that) {
    if (unmodifiable)
		return;
      if (that != null)
          replace(that.merge(this));
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
   * @return a new {@link Yash} with the Entries selected by the BiFunction
   */
  public Yash select(BiFunction<String, Object, Boolean> selector) {
    Yash ret = new Yash(this);
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
     * @return a new {@link Yash} without the Entries selected by the BiFunction
     */
    public Yash reject(BiFunction<String, Object, Boolean> selector) {
      Yash ret = new Yash(this);
      if (selector != null) {
        for (Map.Entry<String, Object> next : entrySet()) {
          if (selector.apply(next.getKey(), next.getValue()))
            ret.delete(next.getKey());
        }
      }
      return ret;
    }
    public Yash each(BiFunction<String, Object, Void> b) {
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
        list.add(new Object[]{k,null});
      } else {
        if (v instanceof Yash) {
          list.add(new Object[] { k, ((Yash)v).toArray() });
        } else if (v instanceof Map) {
          list.add(new Object[] { k, of((Map<?,?>)v).toArray() });
        } else {
          list.add(new Object[] { k, v });
        }
      }
    }
    return list.toArray();
  }
  public Yash stringifyValues() {
    Yash ret = new Yash();
    for (Map.Entry<String, Object> next : entrySet()) {
        ret.put(next.getKey(), String.valueOf(next.getValue()));
    }
    return ret;
  }
  public void selfStringifyValues() {
    if (unmodifiable)
			return;
    Yash ret = new Yash();
    for (Map.Entry<String, Object> next : this.entrySet()) {
      ret.put(next.getKey(), (next.getValue() == null ? "" : next.getValue().toString()) );
    }
    clear();
    selfMerge(ret);
  }

	public String urlEncode() throws IllegalArgumentException, UnsupportedEncodingException {
		return urlEncode("");
	}

  public String urlEncode(String prefix) throws IllegalArgumentException, UnsupportedEncodingException {
  	return Yash.urlEncode(this, prefix);
  }

  @Override public String toString() {
	  return Json.stringify(this, 2);
  }
}
