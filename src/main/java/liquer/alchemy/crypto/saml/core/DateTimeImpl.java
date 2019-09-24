package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.DateTime;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;
import java.util.Locale;

class DateTimeImpl implements DateTime {

    private final String iso8601DateTime;
    private final Calendar calendar;

    DateTimeImpl(String iso8601DateTime) {
        this.iso8601DateTime = iso8601DateTime;
        this.calendar = DatatypeConverter.parseDateTime(iso8601DateTime);
    }

    @Override
    public String getISO8601DateTime() {
        return iso8601DateTime;
    }

    @Override
    public Calendar toCalendar() {
        return calendar;
    }

    @Override
    public Calendar toCalendar(Locale locale) {
        Calendar ret = Calendar.getInstance(calendar.getTimeZone(), locale);
        ret.setTime(calendar.getTime());
        return ret;
    }
}
