package liquer.alchemy.crypto.jwt;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public final class NumericDate extends Date {

	private final static Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	public NumericDate() {
		this(CALENDAR.getTime());
	}
	public NumericDate(long seconds) {
		super(TimeUnit.SECONDS.toMillis(seconds));
	}
	public NumericDate(Date date) {
		super(date == null ? CALENDAR.getTime().getTime(): date.getTime());
	}

	public long getSecondsAsLong() {
		return TimeUnit.MILLISECONDS.toSeconds(getTime());
	}
	public NumericDate addSeconds(long seconds) {
		return new NumericDate(getSecondsAsLong() + seconds);
	}
	@Override
	public String toString() {
		return String.valueOf(getSecondsAsLong());
	}
}
