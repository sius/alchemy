package liquer.alchemy.crypto.sign.v4;

import liquer.alchemy.crypto.CryptoLimericks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignV4Utils {

	private SignV4Utils() {}
	public static Charset getCharsetSafely(Charset charset) {
		return charset == null ? StandardCharsets.UTF_8 : charset;
	}
	public static InputStream getStream(char[] value, InputStream defaultValue, Charset charset) {
		return value == null ? defaultValue : new ByteArrayInputStream(new String(value).getBytes(getCharsetSafely(charset)));
	}
	public static InputStream getStream(String value, InputStream defaultValue, Charset charset) {
		return value == null ? defaultValue : new ByteArrayInputStream(value.getBytes(getCharsetSafely(charset)));
	}
	public static byte[] getBytes(char[] value, byte[] defaultValue) {
		return getBytes(value, defaultValue, null);
	}
	public static byte[] getBytes(char[] value, byte[] defaultValue, Charset charset) {
		return value == null ? defaultValue : new String(value).getBytes(getCharsetSafely(charset));
	}
	public static byte[] getBytes(String value, byte[] defaultValue) {
		return getBytes(value, defaultValue, null);
	}
	public static byte[] getBytes(String value, byte[] defaultValue, Charset charset) {
		return value == null ? defaultValue : value.getBytes(getCharsetSafely(charset));
	}

	final private static String S4_TIME_STAMP_FMT = "yyyyMMdd'T'HHmmss'Z'";
	final private static String S4_DATE_STAMP_FMT = "yyyyMMdd";

	/**
	 * @return yyyyMMdd'T'HHmmss'Z' formatted Date string
	 */
	public static String getS4TimeStamp(Date date) {
		SimpleDateFormat f = new SimpleDateFormat(S4_TIME_STAMP_FMT);
		return f.format(date);
	}

	public static Date parseS4TimeStamp(String timeStamp) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(S4_TIME_STAMP_FMT);
		return f.parse(timeStamp);
	}

	/**
	 * @return yyyyMMdd formatted Date string
	 */
	public static String getS4DateStamp(Date date) {
		SimpleDateFormat f = new SimpleDateFormat(S4_DATE_STAMP_FMT);
		return f.format(date);
	}

	public static Date parseS4DateStamp(String timeStamp) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(S4_DATE_STAMP_FMT);
        return f.parse(timeStamp);
	}

	public static byte[] getS4SignatureKey(String algorithm, CredentialScope scope, byte[] kSecret) {
		byte[] kDate = CryptoLimericks.sign(getS4DateStamp(scope.getDate()), algorithm, kSecret);
		byte[] kRegion = CryptoLimericks.sign(scope.getRegionName(), algorithm, kDate);
		byte[] kService = CryptoLimericks.sign(scope.getServiceName(), algorithm, kRegion);
        return CryptoLimericks.sign(scope.getTerminationString(), algorithm, kService);
	}
}
