package liquer.alchemy.athanor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NestedParamsSupport {

	private NestedParamsSupport() {

	}

	public static final String DEFAULT_SEP = "&";
	/**
	* The default number of bytes to allow parameter keys to take up.
	* This helps prevent a rogue client from flooding a Request.
	*/
	public static final int KEY_SPACE_LIMIT = 65536;


	public static String encodeWwwFormComponent(Object str, String charset) throws UnsupportedEncodingException {
		String ret = "";
		if (str != null) {
			if (charset == null) {
				charset = "UTF-8";
			}
			ret = URLEncoder.encode(str.toString(), charset);
		}
		return ret;
	}

	public static String decodeWwwFormComponent(Object str, String enc) throws UnsupportedEncodingException {
		String ret = "";
		if (str != null) {
			if (enc == null) {
				enc =  "UTF-8";
			}
			ret = URLDecoder.decode(str.toString(), enc);
		}
		return ret;
	}

	/**
	 * URI escapes. (CGI style space to +)
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static String escape(Object o, String enc) throws UnsupportedEncodingException {
		return encodeWwwFormComponent(o, enc);
	}

	public static String escape(Object o) throws UnsupportedEncodingException {
		return escape(o, null);
	}

	/**
	 * Like URI escaping, but with %20 instead of +. Strictly speaking this is
	 * true URI escaping.
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static String escapePath(Object o, String enc) throws UnsupportedEncodingException {
		return escape(o, enc).replaceAll("\\+", "%20");
	}
	
	/**
	 * Unescapes a URI escaped string with +encoding+. +encoding+ will be the
	 * target encoding of the string returned, and it defaults to UTF-8
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 */
	public static String unescape(Object o, String charset) throws UnsupportedEncodingException {
		return decodeWwwFormComponent(o, charset);
	}

	public static String unescape(Object o) throws UnsupportedEncodingException {
		return unescape(o , null);
	}

	public static Yash parseNestedQuery(String qs, Object d) throws UnsupportedEncodingException {
		KeySpaceConstrainedParams params  = new KeySpaceConstrainedParams();
		if (qs == null) qs = "";
		String expr = DEFAULT_SEP;
		if (d != null) {
			expr = String.format("[%1$s]", d);
		}
		String[] pp = qs.split(expr);
		for(String p : pp) {
			String[] kv = p.split("=", 2);
			String key = null, value = null;
			if (kv.length > 0) key = unescape(kv[0]);
			if (kv.length > 1) value = unescape(kv[1]);
			params = (KeySpaceConstrainedParams)normalizeParams(params, key, value);
		}
		return Yash.of(params);
	}

	private static final Pattern np_pattern0 = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");
	private static final Pattern np_pattern1 = Pattern.compile("^\\[\\]\\[([^\\[\\]]+)\\]$");
	private static final Pattern np_pattern2 = Pattern.compile("^\\[\\](.+)$");
	private static Map<String, Object> normalizeParams(Map<String, Object> params, String name, Object v) throws IllegalArgumentException {
		if (params == null) {
			return null;
		}
		String k = "";
		String after = "";
		Matcher m;
		if ((m = np_pattern0.matcher(name)).find()) {
			k = m.group(1);
			if (!m.hitEnd()) after = name.substring(m.end());
		}
		if (k.equals("")) {
			return params;
		}
		if (after.equals("")) {
			params.put(k, v);
		} else if (after.equals("[]")) {
			Object o = params.get(k);
			if (o == null) {
				o = new Object[0];
			}
			if (!o.getClass().isArray()) {
				throw new IllegalArgumentException("expected Array (got "+ o.getClass().getName()+") for param '" + k +"'");
			}
			Object[] old_arr = (Object[])o;
			Object[] new_arr = new Object[old_arr.length+1];
			System.arraycopy(old_arr, 0, new_arr, 0, old_arr.length);
			new_arr[old_arr.length] = v;
			params.put(k, new_arr);
		} else if ((m = np_pattern1.matcher(after)).find() || (m = np_pattern2.matcher(after)).find()) {
			String child_key = m.group();
			Object o = params.get(k);
			if (o == null) {
				o = new Object[0];
			}
			if (!o.getClass().isArray()) {
				throw new IllegalArgumentException("expected Array (got "+ o.getClass().getName()+") for param '" + k +"'");
			}
			Object[] params_arr = (Object[])o;
			if ( params_arr.length > 0 && isMap(params_arr[params_arr.length-1]) && !((Map<?, ?>)params_arr[params_arr.length-1]).containsKey(child_key)) {
				normalizeParams(Yash.of((Map<?, ?>)params_arr[params_arr.length-1]), child_key, v);
			} else {
				Object[] new_params_arr = new Object[params_arr.length+1];
				System.arraycopy(params_arr, 0, new_params_arr, 0, params_arr.length);
				new_params_arr[params_arr.length] = normalizeParams(new KeySpaceConstrainedParams(), child_key, v);
				params.put(k, new_params_arr);
			}
		} else {
			Object o = params.get(k);
			if (o == null) {
				o = new KeySpaceConstrainedParams();
				params.put(k, o);
			}
			if (!isMap(o)) {
				throw new IllegalArgumentException("expected 'Map' (got "+ o.getClass().getName()+") for param '" + k +"'");
			}
		
			params.put(k, normalizeParams(Yash.of((Map<?,?>)o), after, v));
		}
		return params;
	}
	
	private static boolean isMap(Object obj) {
		return (obj instanceof Map);
	}
	
	public static String buildQuery(Object value, String prefix) throws IllegalArgumentException, UnsupportedEncodingException{
		StringBuilder ret = new StringBuilder();
		if (value != null) {
			if (value.getClass().isArray()) {
				int cur_len = Array.getLength(value);
				for (int i = 0; i < cur_len; i++) {
					ret.append(buildQuery(Array.get(value, i), prefix));
				}
			} else if (value instanceof Map) {
				if (prefix == null) {
					prefix = "&";
				}
				Map<?,?> m = (Map<?,?>)value;
				Yash y = Yash.of(m);
				Iterator<Entry<String,Object>> iter = y.entrySet().iterator();
				for (; iter.hasNext(); ) {
					Entry<String,Object> next = iter.next();
					String k = next.getKey();
					Object v = next.getValue();
					ret.append(buildQuery(v, prefix + escape(k)));
				}
				if (ret.length() > 0) {
					ret.delete(0,1);
				}
			} else {
				if (prefix == null) {
					throw new IllegalArgumentException("value must be a 'Map'");
				} else {
					ret.append(prefix).append('=').append(escape(value));
				}
			}
		} else {
			ret.append(prefix);
		}
		return ret.toString();
	}
	public static String buildEncodedNestedQuery(Object value, Function<String, String> encode, String prefix) {
		if (encode == null) {
			encode = (s) -> s ;
		}
		StringBuilder ret = new StringBuilder();
		if (value != null) {
			if (value.getClass().isArray()) {
				int cur_len = Array.getLength(value);
				for (int i = 0; i < cur_len; i++){
					ret.append(buildEncodedNestedQuery(Array.get(value, i), encode, prefix + encode.apply("[]"))).append('&');
				}
				if (ret.length()>0) {
					ret.delete(ret.length()-1, ret.length());
				}
			} else if (value instanceof Map) {
				Yash y = Yash.of((Map<?,?>)value);
				Iterator<Entry<String,Object>> iter = y.entrySet().iterator();
				for (; iter.hasNext(); ) {
					Entry<String,Object> next = iter.next();
					Object k = next.getKey();
					Object v = next.getValue();
					ret.append(buildEncodedNestedQuery(v, encode, prefix != null ? (prefix + '[' + encode.apply(k.toString()) + ']') : encode.apply(k.toString()))).append('&');
				}
				if (ret.length()>0) {
					ret.delete(ret.length()-1, ret.length());
				}
			} else if (value instanceof String) {
				if (prefix == null) {
					throw new IllegalArgumentException("value must be a 'Hash'");
				} else {
					ret.append(prefix).append('=').append(encode.apply(value.toString()));
				}
			} else {
				if (prefix == null) {
					throw new IllegalArgumentException("value must be a 'Hash'");
				} else {
					ret.append(prefix).append('=').append(encode.apply(value.toString()));
				}
			}
		} else {
			ret.append(prefix);
		}
		return ret.toString();
	}
	public static String buildNestedQuery(Object value, String prefix) throws IllegalArgumentException, UnsupportedEncodingException{
		StringBuilder ret = new StringBuilder();
		if (value != null) {
			if (value.getClass().isArray()) {
				int cur_len = Array.getLength(value);
				for (int i = 0; i < cur_len; i++) {
					ret.append(buildNestedQuery(Array.get(value, i), prefix + escape("[]"))).append('&');
				}
				if (ret.length()>0) {
					ret.delete(ret.length()-1, ret.length());
				}
			} else if (value instanceof Map) {
				Yash y = Yash.of((Map<?,?>)value);
				Iterator<Entry<String,Object>> iter = y.entrySet().iterator();
				for (; iter.hasNext(); ) {
					Entry<String,Object> next = iter.next();
					Object k = next.getKey();
					Object v = next.getValue();
					ret.append(buildNestedQuery(v, prefix != null ? (prefix + '[' + escape(k) + ']') : escape(k))).append('&');
				}
				if (ret.length()>0) {
					ret.delete(ret.length()-1, ret.length());
				}
			} else if (value instanceof String) {
				if (prefix == null) {
					throw new IllegalArgumentException("value must be a 'Hash'");
				} else {
					ret.append(prefix).append('=').append(escape(value));
				}
			} else {
				if (prefix == null) {
					throw new IllegalArgumentException("value must be a 'Hash'");
				} else {
					ret.append(prefix).append('=').append(escape(value));
				}
			}
		} else {
			ret.append(prefix);
		}
		return ret.toString();
	}

	public static StringBuffer escape(String input, Pattern escapePattern, Map<String, String> escapeMap) {
		StringBuffer buffer = new StringBuffer();
		Matcher m = escapePattern.matcher(input);
		while(m.find()) {
			m.appendReplacement(buffer, escapeMap.get(m.group()));
		}
		m.appendTail(buffer);
		return buffer;
	}

	static class KeySpaceConstrainedParams implements Map<String, Object> {
	
		private int limit = KEY_SPACE_LIMIT;
		private int size;
		
		private Map<String, Object> params;
		
		KeySpaceConstrainedParams() {
			this(null);
		}
		
		KeySpaceConstrainedParams(Integer limit) {
			super();
			if (limit != null) {
				this.limit = limit;
			}
			size = 0;
			params = new LinkedHashMap<>();
		}
		public Object put(String key, Object value) throws IllegalArgumentException{
			if (key != null && !containsKey(key)) {
				size += key.length();
			}
			if (size > limit) {
				throw new IllegalArgumentException("RangeError, 'exceeded available parameter key space'");
			}
			return params.put(key, value);
		}
		public Object get(Object key) {
			return params.get(key);
		}
		public boolean containsKey(Object key) {
			return params.containsKey(key);
		}
		public void clear() {
			params.clear();
		}
		public boolean containsValue(Object value) {
			return params.containsValue(value);
		}
		public Set<Entry<String, Object>> entrySet() {
			return params.entrySet();
		}
		public boolean isEmpty() {
			return params.isEmpty();
		}
		public Set<String> keySet() {
			return params.keySet();
		}
		public Object remove(Object key) {
			return params.remove(key);
		}
		public int size() {
			return params.size();
		}
		public Collection<Object> values() {
			return params.values();
		}
		public void putAll(Map<? extends String, ?> map) {
			params.putAll(map);
		}
	}
}
