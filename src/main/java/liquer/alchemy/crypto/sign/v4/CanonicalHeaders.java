package liquer.alchemy.crypto.sign.v4;

import liquer.alchemy.collection.Yash;
import liquer.alchemy.util.Tuple.Tuple2;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CanonicalHeaders implements Iterable<Entry<String, Object>>{

	final private static Pattern TRIM_PATTERN = Pattern.compile("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)");

	public static String trim(Object value) {
		return value == null ? "" : trim(value.toString());
	}
	/**
	 * 1. Trims blanks but preserves 1 blank between other characters, e.g,:
	 * "   a   b   c    " becomes "a b c"
	 * 2. Trims blanks but preserves any blanks between quotes, e.g.:
	 * "   \"   a   b   c   \"    "  becomes
	 * "\"   a   b   c   \""
	 *
	 * "   a   b   \"   a   b   c   \"   c   d   \"   a   b   c   \"     e " becomes
	 * "a b \"   a   b   c   \" c d \"   a   b   c   \" e"
	 * @param value the value to trim
	 * @return the trimmed value
	 */
	public static String trim(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		Matcher m;
		StringBuffer buffer = new StringBuffer();
		if((m = TRIM_PATTERN.matcher(value.trim())).find()) {
			m.appendReplacement(buffer, " ");
			while (m.find())
				m.appendReplacement(buffer," ");
			m.appendTail(buffer);
		} else {
			buffer.append(value.trim());
		}
		return buffer.toString();
	}
	private Map<String, Object> headers;
	public boolean containsHeader(String header) {
		return headers.containsKey(header);
	}
	public CanonicalHeaders(String host) {
		this(host, null);
	}
	public CanonicalHeaders(String host, Map<String, Object> headers) {
		this.headers = Yash.of("Host", host).merge(new Yash(headers));
	}
	@Override public String toString() {

		Comparator<Tuple2<String, Object>> comparator = (t1, t2) -> {
			if (t1 == null) {
				return -1;
			}
			if (t2 == null) {
				return 1;
			}
			return t1._1.compareTo(t2._1);
		};

		// do not use platform specific line separator '%n' in this String.format
		return StreamSupport.stream(this.spliterator(), false)
				.map(arg0 -> new Tuple2<>(arg0.getKey().toLowerCase(), arg0.getValue()))
				.sorted(comparator)
				.map(t -> String.format("%1$s:%2$s\n", t._1, trim(t._2)))
				.collect(Collectors.joining());
	}

	public String getSignedHeaders() {
		return StreamSupport.stream(this.spliterator(), false)
				.map(arg0 -> arg0.getKey().toLowerCase())
				.sorted()
				.collect(Collectors.joining(";"));
	}
	@Override public Iterator<Entry<String, Object>> iterator() {
		return headers.entrySet().iterator();
	}
}
