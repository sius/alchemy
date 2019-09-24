package liquer.alchemy.athanor;

import liquer.alchemy.json.JsonDateFormat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class FurnaceConfig {

	/**
	 * RFC1123
	 */
	private static final Locale DEFAULT_LOCALE = Locale.US;
	
	@Attr
	private String dateFormat = null;
	@Attr
	private String language = null;
	@Attr
	private String country = null;
	@Attr
	private String variant = null;
	@Attr
	private String timeZone = null;
	@Attr
	private String binaryEncoding = null;
	@Attr
	private String charset= null;
	@Attr
	private Integer defaultIndent = null;
	@Attr
	private Integer renderDepth = null;
	@Attr
	private Boolean includeSuperClasses = null;

	/**
	 * @return configured dateFormat or ISO8601: yyyy-MM-dd'T'HH:mm:ss.SSSXXX
	 */
	public SimpleDateFormat getSimpleDateFormat() {
		return dateFormat != null
			? new JsonDateFormat(dateFormat, getTimeZone(), getLocale())
			: getDefaultSimpleDateFormat();
	}
	public SimpleDateFormat getDefaultSimpleDateFormat() {

		return new JsonDateFormat(getTimeZone(), getLocale());
	}
	/**
	 * @return configured Locale or Locale.US
	 */
	public Locale getLocale() {
		return (language != null)
		 ?  new Locale(language, country, variant)
		 : DEFAULT_LOCALE;
	}
	/**
	 * @return configured TimeZone or Default TimeZone
	 */
	public TimeZone getTimeZone() {
		return (timeZone != null) 
			? TimeZone.getTimeZone(timeZone)
			: TimeZone.getDefault();
	}
	
	public Charset getCharset() {
		if (charset == null) {
			 return StandardCharsets.UTF_8;
		}
		try {
			return Charset.forName(charset);
		} catch (IllegalArgumentException e) {
			return StandardCharsets.UTF_8;
		}
	}
	public int getDefaultIndent() {
		return defaultIndent == null 
			? 2 
			: Math.max(0, defaultIndent);
	}
	public int getRenderDepth() {
		return renderDepth == null
			? 64
			: Math.max(1, renderDepth);
	}

	public Boolean getIncludeSuperClasses() {
		return includeSuperClasses;
	}

	public String getBinaryEncoding() {
		return binaryEncoding;
	}
}
