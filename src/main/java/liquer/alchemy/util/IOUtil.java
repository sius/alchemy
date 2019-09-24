package liquer.alchemy.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class IOUtil {

	private IOUtil() {}

	public static char[] toCharArray(InputStream is) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output);
		return output.toCharArray();
	}
	public static char[] toCharArray(InputStream is, int maxlength) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output, maxlength);
		return output.toCharArray();
	}
	public static char[] toCharArray(InputStream is, String encoding) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output, encoding);
		return output.toCharArray();
	}
	public static char[] toCharArray(InputStream is, String encoding, int maxlength) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy(is, output, encoding, maxlength);
		return output.toCharArray();
	}
	public static char[] toCharArray(Reader input) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy(input, sw);
		return sw.toCharArray();
	}
	public static char[] toCharArray(Reader input, int maxlength) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy(input, sw, maxlength);
		return sw.toCharArray();
	}
	public static String toString(URL url) throws IOException {
		return toString(url.getFile());
	}

	public static String toString(String filePath) throws IOException {
		return toString(new File(filePath));
	}

	public static String toString(File f) throws IOException {
		return toString(new FileInputStream(f));
	}

	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}
	public static String toString(InputStream input, int maxlength) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, maxlength);
		return sw.toString();
	}
	public static String toString(InputStream input, String encoding) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding);
		return sw.toString();
	}
	public static String toString(InputStream input, String encoding, int maxlength) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding, maxlength);
		return sw.toString();
	}
	public static String toString(Reader input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}
	public static String toString(Reader input, int maxlength) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, maxlength);
		return sw.toString();
	}
	private final static int DEFAULT_tBUFFER_tSIZE = 4096;
	
	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_tBUFFER_tSIZE];
		long count = 0;
		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	public static int copy(Reader input, Writer output) throws IOException {
		return copy(input, output, Integer.MAX_VALUE);
	}
	public static int copy(Reader input, Writer output, int maxLength) throws IOException {
		long count = copyLarge(input, output);
		if (count > maxLength) {
			return -1;
		}
		return (int) count;
	}
	public static void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input, StandardCharsets.UTF_8);
		copy(in, output);
	}
	public static void copy(InputStream input, Writer output, int maxlength) throws IOException {
		InputStreamReader in = new InputStreamReader(input, StandardCharsets.UTF_8);
		copy(in, output, maxlength);
	}
	public static void copy(InputStream input, Writer output, String charset) throws IOException {
		copy(input,output, charset, Integer.MAX_VALUE);
	}
	public static void copy(InputStream input, Writer output, String charset, int maxlength) throws IOException {
		if (charset == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, Charset.forName(charset));
			copy(in, output, maxlength);
		}
	}

	/**
	 * @param input an InputStream
	 * @return an empty byte[0] array if input is null
	 * @throws IOException IOException
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
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
	public static byte[] toByteArrayQuietly(InputStream input) {
		try {
			return toByteArray(input);
		} catch (IOException ignore) {
		}
		return null;
	}
	public static void toOutputStream(InputStream input, OutputStream output) throws IOException {
		if (output == null || input == null) return;
		try {
			byte[] buff = new byte[4096];
			int read;
			while ((read = input.read(buff, 0, buff.length)) > -1)
				output.write(buff, 0, read);
			output.close();
		} finally {
			input.close();
		}
	}
	public static void closeQuietly(Closeable c) {
		try {
			if (c != null)
				c.close();
		} catch (Throwable ignore) {  }
	}
	public static void write(byte[] val, OutputStream out) throws IOException{
		if (out == null) return;
		if (val == null) return;
		out.write(val);
	}
	public static void writeCloseQuietly(byte[] val, OutputStream out) {
		if (out == null) return;
		if (val == null) return;
		try {
			out.write(val);
		} catch (IOException e) {
			closeQuietly(out);
		}
	}
	/**
	 * default value for maxBufferSize is 64 KB
	 * @param length length
	 * @param maxBufferSize maxBufferSize
	 * @return the calculated buffer size
	 */
	public static int calcStreamBufferSize(long length, int maxBufferSize) {
		maxBufferSize = Math.max(1024 << 6, maxBufferSize);
		for (int i = 12, _t = 1024 << i; i > 2; i--, _t = _t >> 1) {
			if (_t <= length) return Math.min(maxBufferSize, _t);
		}
		return 4096; // 1024 << 2
	}
	public static int calcStreamBufferSize(long length) {
		return calcStreamBufferSize(length, 1024 << 6);
	}
	/**
	 * default value for maxBufferSize is 64 KB
	 * @param length length
	 * @param maxBufferSize maxBufferSize
	 * @return the calculated buffer size
	 */
	public static int calcStreamBufferSize(Double length, int maxBufferSize) {
		return calcStreamBufferSize(length == null || length < 0 ? 0L : length.longValue(), maxBufferSize);
	}
	/**
	 * default value for maxBufferSize is 64 KB
	 * @param length length
	 * @return the calculated buffer size
	 */
	public static int calcStreamBufferSize(Double length) {
		return calcStreamBufferSize(length == null || length < 0 ? 0L : length.longValue(), 1024 << 6);
	}
	
}
