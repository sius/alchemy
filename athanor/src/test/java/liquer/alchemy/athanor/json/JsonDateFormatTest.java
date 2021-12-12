package liquer.alchemy.athanor.json;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonDateFormatTest {
	@Test
	void format() {
		String expected = "1970-01-01T12:00:00.000+00:00";

		String actual = new JsonDateFormat(TimeZone.getTimeZone("GMT"), Locale.US).format(new Date(0));

		assertEquals(expected, actual);
	}

	@Test
	void parse() throws ParseException {
		String expected = "1970-01-01T12:00:00.000+00:00";

		Date actual = new JsonDateFormat(TimeZone.getTimeZone("GMT"), Locale.US).parse(expected);

		assertEquals(0, actual.getTime());
	}

}
