package liquer.alchemy.json;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JsonDateFormat extends SimpleDateFormat {

	public JsonDateFormat() {
		this("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", TimeZone.getDefault(), Locale.getDefault());
	}

	public JsonDateFormat(TimeZone timeZone, Locale locale) {
		this("yyyy-MM-dd'T'hh:mm:ss.SSSXXX", timeZone, locale);
	}

	public JsonDateFormat(String dateFormat, TimeZone timeZone, Locale locale) {
		super(dateFormat, locale);
		this.setTimeZone(timeZone == null ? TimeZone.getDefault() : timeZone);

	}

	@Override
	public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
		StringBuffer ret = super.format(date, stringBuffer, fieldPosition);
		String val = ret.toString();
		if (val.endsWith("Z")) {
			ret = new StringBuffer(val.substring(0,val.length() - 1)).append("+00:00");
		}
		return ret;
	}

	@Override
	public Date parse(String s, ParsePosition parsePosition) {

		if (s.endsWith("+00:00") || s.endsWith("+0000")) {
			int pos = s.lastIndexOf("+00");
			s = s.substring(0, pos) + "Z";
		}

		return super.parse(s, parsePosition);
	}
}


