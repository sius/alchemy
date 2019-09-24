package liquer.alchemy.crypto.saml;

import java.util.Calendar;
import java.util.Locale;

public interface DateTime {

    String getISO8601DateTime();

    Calendar toCalendar();

    Calendar toCalendar(Locale locale);
}
