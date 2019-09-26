package liquer.alchemy.athanor.json;

import liquer.alchemy.athanor.reflect.*;
import liquer.alchemy.support.BaseN;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the abstract base class for JSON types:
 * {@link JNull}, {@link JBool}, {@link JNum}, {@link JStr}, {@link JBin}, {@link JSeq} and {@link JObj}
 */
public abstract class Json {

	private static final Logger LOG = LogManager.getLogger(Json.class);


	public static final String DATA 	= "data";

	public static class Singleton {
		public static final FurnaceConfig CONFIG = FurnaceManager.getInstance().getConfig();
		public static final Json NULL      = new JNull();
		public static final Json TRUE      = new JBool(true);
		public static final Json FALSE 	   = new JBool(false);
		public static final Json ZERO 	   = new JNum(0.0);
		public static final Json MAX_VALUE = new JNum(Double.MAX_VALUE);
		public static final Json MIN_VALUE = new JNum(Double.MIN_VALUE);
		public static final Json EMPTY_STR = new JStr("");
		public static final Json EMPTY_SEQ = new JSeq(new ArrayList<>());
		public static final Json EMPTY_OBJ = new JObj(null);
		public static final Json EMPTY_BIN = new JBin(new byte[0]);
	}

	/**
	 * Represents a JSON null: "null"
	 */
	public static final class JNull extends Json {
		public static final Json NULL = Singleton.NULL;
		private JNull() { }
		@Override public String toString() { return "null"; }
	}

	/**
	 * Represents a JSON bool: either "true" or "false"
	 */
	public static final class JBool extends Json {
		public static final Json FALSE = Singleton.FALSE;
		public static final Json TRUE = Singleton.TRUE;

		private final boolean value;
		private JBool(boolean value) { this.value = value; }
		public String toString() {  return value ? "true" : "false"; }
	}
	/**
	 * Represents a JSON Number: a double value
	 */
	public static class JNum extends Json {
		public static final Json ZERO = Singleton.ZERO;
		public static final Json MAX_VALUE = Singleton.MAX_VALUE;
		public static final Json MIN_VALUE = Singleton.MIN_VALUE;

		private final double value;
		public JNum(double value) { this.value = value; }
		@Override public String toString() {
			return String.valueOf(value);
		}
	}
	/**
	 * Represents a base64 encoded stream or binary
	 */
	public static class JBin extends Json {

		public static final Json EMPTY = Singleton.EMPTY_BIN;
		private final String value;
		public JBin(byte[] value) { this.value = value == null ? null : "\"" + BaseN.base64Encode(value).replace("/", "\\/") + "\""; }
		public JBin(InputStream inputStream) { this(toByteArrayQuietly(inputStream)); }
		@Override
		public String toString() {  return value == null ? Singleton.NULL.toString() : value; }
		/**
		 * @param input the Input Stream
		 * @return an empty byte[0] array if input is null
		 * @throws IOException cannot convert InputStream to byte array
		 */
		private static byte[] toByteArray(InputStream input) throws IOException {
			if (input == null)
				return new byte[0];
			byte[] buff = new byte[4096];
			int read;

			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			while ((read = input.read(buff, 0, buff.length)) > -1) {
				dest.write(buff, 0, read);
			}
			return dest.toByteArray();
		}
		private static byte[] toByteArrayQuietly(InputStream input) {
			try {
				return toByteArray(input);
			} catch (IOException ignore) {
			}
			return null;
		}
	}
	/**
	 * Represents a JSON String wrapped in quote marks "...".
	 * <ul>Special characters will be replaced as follows:
	 * <li>\u0022 or '"'  :=  "\\\\"</li>
	 * <li>\u005C or '\\' :=  "\\/"</li>
	 * <li>\u2215 or '/'  :=  "\\/"</li>
	 * <li>\u0008 or '\b' :=  "\\b"</li>
	 * <li>\u000C or '\f' :=  "\\f"</li>
	 * <li>\u000A or '\n' :=  "\\n"</li>
	 * <li>\u000D or '\r' :=  "\\r"</li>
	 * <li>\u0009 or '\t' :=  "\\t"</li>
	 * </ul>
	 */
	public static class JStr extends Json {

		public static final Json EMPTY = Singleton.EMPTY_STR;

		private String str;
		public JStr(String str) { this.str = str; }
		@Override public String toString() {
			return (str == null)
				? Singleton.NULL.toString()
				: "\"" +
				str.replace("\\", "\\\\").
					replace("\"", "\\\"").
					replace("/", "\\/").
					replace("\b", "\\b").
					replace("\f", "\\f").
					replace("\n", "\\n").
					replace("\r", "\\r").
					replace("\t", "\\t") + "\"";
			}
	}

	/**
	 * Represents a JSON array or sequence value: a {@link Collection} with {@link Json} items
	 */
	public static class JSeq extends Json {

		public static final Json EMPTY = Singleton.EMPTY_SEQ;

		private Stream<Json> elems;

		private String mkstr;
		public JSeq(Stream<Json> elems) { this.elems =  elems; }
		public JSeq(Collection<Json> elems) { this.elems =  elems == null ? null : elems.stream(); }
		public String toString() {
			if (elems == null)
				return Singleton.NULL.toString();
			else {
				if (mkstr == null) {
					mkstr = elems.map(Json::toString).collect(Collectors.joining(",", "[", "]"));
				}
				return mkstr;
			}
		}
		@Override public void print(JsonWriter writer) {
			if (elems == null) {
				writer.print(Singleton.NULL.toString());
			} else {
				writer.beginSeq();
				Iterator<Json> iter = elems.iterator();
				if (iter.hasNext()) {
					writer.value(iter.next());
					while(iter.hasNext()) {
						writer.comma();
						writer.value(iter.next());
					}
				}
				writer.endSeq();
				writer.flush();
			}
		}
	}

	/**
	 * Represents a JSON object value: a {@link Map} with {@link String} keys and {@link Json} values
	 */
	public static class JObj extends Json {
		public static final Json EMPTY = Singleton.EMPTY_OBJ;
		private Map<String, Json> bindings;
		private String mkstr;

		public JObj(Map<String, Json> bindings) {
			this.bindings = bindings == null ? new HashMap<>() : bindings;
		}
		@Override public String toString() {
			if (bindings == null)
				return Singleton.NULL.toString();
			else {
				if (mkstr == null) {
					mkstr = bindings.entrySet().stream()
						.map(x -> String.format("\"%1$s\":%2$s", x.getKey(), x.getValue()))
						.collect(Collectors.joining(",", "{", "}"));
				}
				return mkstr;
			}
		}
		@Override public void print(JsonWriter writer) {
			if (bindings == null) {
				writer.print(Singleton.NULL.toString());
			} else {
				writer.beginObj();
				Iterator<Entry<String, Json>> iter = bindings.entrySet().iterator();
				if (iter.hasNext()) {
					Entry<String, Json> next = iter.next();
					writer.namedValue(next.getKey(), next.getValue());
					while(iter.hasNext()) {
						next = iter.next();
						writer.comma();
						writer.namedValue(next.getKey(), next.getValue());
					}
				}
				writer.endObj();
				writer.flush();
			}
		}
	}

	public static Json bool(Boolean b) {
		if (b == null) {
			return Singleton.NULL;
		}
		return b ? Singleton.TRUE : Singleton.FALSE;
	}
	public static Json num(Number num) {
		if (num == null) {
			return Singleton.NULL;
		}
		return  num.doubleValue() == 0 ? Singleton.ZERO : new JNum(num.doubleValue());
	}
	public static Json num(Number num, Class<?> k) {
		if (num == null) {
			return Singleton.NULL;
		} else if (Long.class.isAssignableFrom(k)) {
			return str(String.valueOf(num));
		} else if (BigDecimal.class.isAssignableFrom(k)) {
			return str(String.valueOf(num));
		} else if (BigInteger.class.isAssignableFrom(k)) {
			return str(String.valueOf(num));
		} else {
			return num.doubleValue() == 0 ? Singleton.ZERO : new JNum(num.doubleValue());
		}
	}
	public static Json str(String str) {
		if (str == null) {
			return Singleton.NULL;
		}
		return str.equals("") ? Singleton.EMPTY_STR : new JStr(str);
	}
	public static Json str(Date date) {
		return date == null ? Singleton.NULL : new JStr(Singleton.CONFIG.getSimpleDateFormat().format(date));
	}
	public static Json bin(InputStream stream) {
		return stream == null ? Singleton.NULL : new JBin(stream);
	}
	public static Json seq(Object arr, int depth) {
		Collection<Json> ret = new ArrayList<>();
		if (arr.getClass().isArray()) {
			int len = Array.getLength(arr);
			for (int i = 0; i < len; i++)
				ret.add(obj(Array.get(arr, i), depth-1, false));
		}
		return new JSeq(ret);
	}
	public static Json seq(Enumeration<?> enumeration, int depth) {
		Collection<Json> ret = new ArrayList<>();
		while(enumeration.hasMoreElements()) {
			ret.add(obj(enumeration.nextElement(), depth-1, false));
		}
		return new JSeq(ret);
	}
	public static Json seq(Iterable<?> i, int depth) {
		return seq(i.iterator(), depth);
	}
	public static Json seq(Iterator<?> iter, int depth) {
		Collection<Json> ret = new ArrayList<>();
		while(iter.hasNext())
			ret.add(obj(iter.next(), depth-1, false));
		return new JSeq(ret);
	}
	public static Json seq(Stream<?> stream, int depth) {
		return new JSeq(stream.map(next -> obj(next, depth-1, false)));
	}
	public static Json obj(Map<?,?> map, int depth) {
		Map<String, Json> bindings = new LinkedHashMap<>();
		if (depth > 0 && map != null) {
			map.forEach((key, value) -> bindings.put(key.toString(), obj(value, depth - 1)));
		}
		return new JObj(bindings);
	}
	public static Json obj(Object obj) {
		return obj(obj, Singleton.CONFIG.getRenderDepth(), false);
	}
	public static Json obj(Object obj, int depth) {
		return obj(obj, depth, false);
	}
	public static Json obj(Object obj, int depth, boolean includeSuperclasses) {
		Json ret = Singleton.NULL;
		if (depth > 0 && obj != null) {
			Class<?> k = obj.getClass();
			if (InputStream.class.isAssignableFrom(k)) ret = bin((InputStream)obj);
			else if (k.isArray()) ret = seq(obj, depth);
			else if (Iterable.class.isAssignableFrom(k)) ret = seq((Iterable<?>)obj, depth);
			else if (Iterator.class.isAssignableFrom(k)) ret = seq((Iterator<?>)obj, depth);
			else if (Enumeration.class.isAssignableFrom(k)) ret = seq((Enumeration<?>)obj, depth);
			else if (Stream.class.isAssignableFrom(k)) ret = seq((Stream<?>)obj, depth);
			else if (Map.class.isAssignableFrom(k)) ret = obj((Map<?,?>)obj, depth);
			else if (k.equals(String.class) || k.equals(Character.class)) ret = str(obj.toString());
			else if (k.isEnum()) ret = str(obj.toString());
			else if (k.equals(Boolean.class)) ret = bool((Boolean)obj);
			else if (Number.class.isAssignableFrom(k)) ret = num((Number)obj, k);
			else if (Json.class.isAssignableFrom(k)) ret = (Json)obj;
			else if (Date.class.isAssignableFrom(k)) ret = str((Date)obj);
			else if (Class.class.isAssignableFrom(k)) ret = str(((Class<?>)obj).getCanonicalName());
			// else if (k.equals(Void.class)) {
				// no operation
			// }
			else {
				ret = reflect(obj, k, new LinkedHashMap<>(), depth, includeSuperclasses);
				if (ret.equals(Singleton.EMPTY_OBJ)) {
					ret = str(obj.toString());
				}
			}
		}
		return ret;
	}

	/**
	 * @param value the JSON string representation
	 * @return true if value argument is a valid JSON representation
	 */
	public static boolean isValid(String value) {
		if (value == null || value.trim().length() == 0) return false;
		try (JsonReader jr = new JsonReader()) {
			for (char c : value.toCharArray()) {
				if (!jr.check(c)) return false;
			}
			return jr.finalCheck();
		} catch (IOException e) {
			return false;
		}
	}

	public static <P> P assign(P projection, Object... values) {
		return assign(null, projection, values);
	}
	public static <P> P assign(final Athanor athanor, P projection, Object... values) {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = projection;
		for (Object data : values) {
			ret = a.transmute(ret, data);
		}
		return ret;
	}
	public static <P> P reassign(P projection, Object... values) {
		return reassign(null, projection, values);
	}
	public static <P> P reassign(final Athanor athanor, P projection, Object... values) {
		return assign(athanor, assign(athanor, projection, values), projection);
	}

	public static <P> P assign(P projection, String... jsonStrValues) {
		return assign(null, projection, jsonStrValues);
	}
	public static <P> P assign(final Athanor athanor, P projection, String... jsonStrValues) {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = projection;
		for (String data : jsonStrValues) {
			ret = a.transmute(ret, parse(data));
		}
		return ret;
	}
	public static <P> P reassign(P projection, String... jsonStrValues) {
		return reassign(null, projection, jsonStrValues);
	}
	public static <P> P reassign(final Athanor athanor, P projection, String... jsonStrValues) {
		return assign(athanor, assign(athanor, projection, jsonStrValues), projection);
	}

	public static <P> P assign(P projection, InputStream... values) throws IOException {
		return assign(null, projection, values);
	}
	public static <P> P assign(final Athanor athanor, P projection, InputStream... values) throws IOException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = projection;
		for (InputStream data : values) {
			ret = a.transmute(ret, Json.parse(data));
		}
		return ret;
	}

	public static <P> P reassign(P projection, InputStream... values) throws IOException {
		return reassign(null, projection, values);
	}
	public static <P> P reassign(final Athanor athanor, P projection, InputStream... values) throws IOException {
		return assign(athanor, assign(athanor, projection, values), projection);
	}

	public static <P> P assign(Class<P> projection, String... jsonStrValues) {
		return assign(null, projection, jsonStrValues);
	}
	public static <P> P assign(final Athanor athanor, Class<P> projection, String... jsonStrValues) {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = null;
		try {
			ret = TypeMapper.newInstance(projection);
			for (String data : jsonStrValues) {
				ret = a.transmute(ret, Json.parse(data));
			}
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			LOG.error(e);
		}
		return ret;
	}
	public static <P> P assign(Class<P> projection, Object... values) {
		return assign(null, projection, values);
	}
	public static <P> P assign(final Athanor athanor, Class<P> projection, Object... values) {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = null;
		try {
			ret = TypeMapper.newInstance(projection);
			for (Object data : values) {
				ret = a.transmute(ret, data);
			}
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			LOG.error(e);
		}
		return ret;
	}

	public static <P> P assign(Class<P> projection, InputStream... values) throws IOException {
		return assign(null, projection, values);
	}
	public static <P> P assign(final Athanor athanor, Class<P> projection, InputStream... values) throws IOException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = null;
		try {
			ret = TypeMapper.newInstance(projection);
			for (InputStream data : values) {
				ret = a.transmute(ret, Json.parse(data));
			}
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			LOG.error(e);
		}
		return ret;
	}

	public static <P> P assignStrict(P projection, String... jsonStr) throws ParseException {
		return assignStrict(null, projection, jsonStr);
	}
	public static <P> P assignStrict(final Athanor athanor, P projection, String... jsonStr) throws ParseException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = projection;
		for (String data : jsonStr) {
			ret = a.transmute(ret, Json.parseStrict(data));
		}
		return ret;
	}

	public static <P> P assignStrict(P projection, InputStream... values) throws IOException, ParseException {
		return assignStrict(null, projection, values);
	}
	public static <P> P assignStrict(final Athanor athanor, P projection, InputStream... values) throws IOException, ParseException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = projection;
		for (InputStream data : values) {
			ret = a.transmute(ret, Json.parseStrict(data));
		}
		return ret;
	}

	public static <P> P assignStrict(Class<P> projection, String... jsonStrValues) throws ParseException {
		return assignStrict(null, projection, jsonStrValues);
	}
	public static <P> P assignStrict(final Athanor athanor, Class<P> projection, String... jsonStrValues) throws ParseException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = null;
		try {
			ret = TypeMapper.newInstance(projection);
			for (String data : jsonStrValues) {
				ret = a.transmute(ret, Json.parseStrict(data));
			}
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			LOG.error(e);
		}
		return ret;
	}

	public static <P> P assignStrict(Class<P> projection, InputStream... values) throws IOException, ParseException {
		return assignStrict(null, projection, values);
	}
	public static <P> P assignStrict(final Athanor athanor, Class<P> projection, InputStream... values) throws IOException, ParseException {
		final Athanor a = (athanor == null) ? Athanor.getInstance() : athanor;
		P ret = null;
		try {
			ret = TypeMapper.newInstance(projection);
			for (InputStream data : values) {
				ret = a.transmute(ret, Json.parseStrict(data));
			}
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			LOG.error(e);
		}
		return ret;
	}

	public static Object parse(String jsonStr) {
		try {
			return parse(new ByteArrayInputStream(jsonStr.getBytes(Singleton.CONFIG.getCharset())));
		} catch (IOException e) {
			LOG.error(e);
			return null;
		}
	}

	public static Object parse(InputStream in) throws IOException {
        try (JsonReader jr = new JsonReader(in, 0)) {
			return jr.read();
		}
    }

	public static Object parseStrict(String jsonStr) throws ParseException {
		try {
			return parseStrict(new ByteArrayInputStream(jsonStr.getBytes(Singleton.CONFIG.getCharset())));
		} catch (IOException e) {
			LOG.error(e);
			return null;
		}
	}
	public static Object parseStrict(InputStream in) throws IOException, ParseException {
        try (JsonReader jr = new JsonReader(in, 0)) {
			return jr.readCarefully();
		}
    }

	/**
	 * Make a String from the Object argument
	 * @param obj tho object argument
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj) {
		return stringify(obj, (Integer)null);
	}

	/**
	 * Make a JSON String from the Object argument
	 * @param obj the object to stringify
	 * @param indent the indent to prettify the JSON String
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj, Integer indent) {
		return stringify(obj, indent, Singleton.CONFIG.getCharset(), false);
	}

	/**
	 * Make a String from the Object argument
	 * @param obj the object to stringify
	 * @param indent the indent to prettify the JSON representation
	 * @param charset the charset to encode with
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj, Integer indent, String charset) {
		return stringify(obj, indent, charset, false);
	}

	/**
	 * Make a String from the Object argument
	 * @param obj the object to stringify
	 * @param charset the charset to encode with
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj, Charset charset) {
		return stringify(obj, null, charset, false);
	}

	/**
	 * Make a String from the Object argument
	 * @param obj the object to stringify
	 * @param indent the indent to prettify the JSON representation
	 * @param charsetName the charsetName to encode with
	 * @param includeSuperclasses render Superclass keys and values if true
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj, Integer indent, String charsetName, boolean includeSuperclasses) {
		return stringify(obj, indent, Charset.forName(charsetName), includeSuperclasses);
	}

	/**
	 * Make a String from the Object argument
	 * @param obj the object to stringify
	 * @param indent he indent to prettify the JSON representation
	 * @param charset the charset to encode with
	 * @param includeSuperclasses render Superclass keys and values if true
	 * @return the JSON representation as String
	 */
	public static String stringify(Object obj, Integer indent, Charset charset, boolean includeSuperclasses) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		JsonWriter writer = indent == null
			? new JsonWriter(bao, charset)
			: new JsonIndentWriter(bao, indent, charset);

		writeTo(obj, writer, Singleton.CONFIG.getRenderDepth(), includeSuperclasses);
		return new String(bao.toByteArray(), charset);
	}

	public static void writeTo(Object obj, OutputStream out) {
		writeTo(
			obj,
			new JsonWriter(out),
			Singleton.CONFIG.getRenderDepth(),
			false);
	}
	public static void writeTo(Object obj, OutputStream out, Integer indent) {
		writeTo(
			obj,
			indent == null
				? new JsonWriter(out)
				: new JsonIndentWriter(out, indent),
			Singleton.CONFIG.getRenderDepth(),
			false);
	}

	public static void writeTo(Object obj, JsonWriter writer) {
		writeTo(obj, writer, Singleton.CONFIG.getRenderDepth(), false);
	}
	public static void writeTo(Object obj, JsonWriter writer, int maxDepth)  {
		writeTo(obj, writer, maxDepth, false);
	}
	public static void writeTo(Object obj, JsonWriter writer, int maxDepth, boolean includeSuperclasses){
		obj(obj, maxDepth, includeSuperclasses).print(writer);
		writer.flush();
	}
	public static void print(Object obj) {
		writeTo(obj, System.out, 2);
	}
	public static void println(Object obj) {
		writeTo(obj, System.out, 2);
		System.out.println();
	}

	private static Collection<ISource> getSourceCollection(Class<?> klass) {
		Map<String, ISource> ret = new LinkedHashMap<>();
		Method[] methods = klass.getDeclaredMethods();
		for (Method _t : methods) {
			ISource source = MethodSource.getInstance(_t);
			if (source != null  && !ret.containsKey(source.getName())) {
				ret.put(source.getName(), source);
			}
		}
		Field[] fields = klass.getDeclaredFields();
		for (Field _t : fields) {
			ISource source = FieldSource.getInstance(_t);
			if (source != null && !ret.containsKey(source.getName())) {
				ret.put(source.getName(), source);
			}
		}
		return ret.values();
	}
	private static Json reflect(Object obj, Class<?> k, final Map<String, Json> bindings, int depth, boolean includeSuperclasses) {
		addObjBindings(bindings, obj,  getSourceCollection(k), depth);
		if (includeSuperclasses) {
			Class<?> superK = k.getSuperclass();
			while (superK != null && !superK.equals(Object.class)) {
				addObjBindings(bindings, obj,  getSourceCollection(superK), depth);
				superK = superK.getSuperclass();
			}
		}
		return bindings.isEmpty() ? Singleton.EMPTY_OBJ : new JObj(bindings);
	}
	private static void addObjBindings(Map<String, Json> bindings, Object obj, Collection<ISource> sources, int depth) {
		for (ISource source : sources) {
			bindings.put(source.getName(), obj(source.getValue(obj), depth-1));
		}
	}

	public void print(JsonWriter writer) { writer.print(toString()); }
	public abstract String toString();
}
