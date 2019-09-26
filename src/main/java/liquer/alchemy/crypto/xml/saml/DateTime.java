package liquer.alchemy.crypto.xml.saml;

import java.util.Calendar;
import java.util.Locale;

public interface DateTime {

    String getISO8601DateTime();

    Calendar toCalendar();

    Calendar toCalendar(Locale locale);
}
