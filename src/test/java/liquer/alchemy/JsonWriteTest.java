package liquer.alchemy;

import liquer.alchemy.athanor.Athanor;
import liquer.alchemy.athanor.Attr;
import liquer.alchemy.json.Json;
import org.junit.Test;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonWriteTest {

	/*
	 * {"FormularID":"200013","Abschlusszeitpunkt":"Mon, 08 Sep 2014 00:00:00.000 GMT","Absendezeitpunkt":"Fri, 08 Aug 2014 00:00:00.000 GMT","AntragstellerName":"BannertAltA, Sabine","AntragstellerSID":null,"VorlagenName":"Virenpruefung","Stichwoerter":"Ausschluss \\"Test\\" Ausschluss \\"Test\\" Geschenk \\"Test\\"","LeistungsempfaengerNamen":["Jung, Rainer","Wuerfel-Hoefling, Sandra"],"LeistungsempfaengerSIDs":["S-1-5-21-38895556-1463316865-1769025822-2729","S-1-5-21-38895556-1463316865-1769025822-144600"],"BearbeiterUndKenntnisnehmerNamen":["Chaouch, Chaouki","Guttenberger, Sonja"],"BearbeiterUndKenntnisnehmerSIDs":["S-1-5-21-38895556-1463316865-1769025822-17529","S-1-5-21-38895556-1463316865-1769025822-5866"],"DruckbildUrl":"https:\/\/filenett.bk.datev.de\/ecm\/workflow\/archiv\/formulare\/200013.pdf","DruckbildName":"Test PDF Archiv Formulare","OriginaldateiUrl":"https:\/\/filenett.bk.datev.de\/ecm\/workflow\/archiv\/formulare\/200013.xml","OriginaldateiName":"Test Org XML Archiv Formulare","Anlagen":[{"Name":"datev_logo","Url":"https:\/\/filenett.bk.datev.de\/ecm\/workflow\/archiv\/formulare\/200013\/attachments\/{6074591C-81A1-4BD3-9871-97DDFC923EC4}"}]}
	 	case '"':  return t._1 + "\\\""; // \u0022
								//case '\'': return t._1 + "\\'";  // \u0027
								case '\\': return t._1 + "\\\\"; // \u005C
								case '/':  return t._1 + "\\/"; // \u2215
								case '\b': return t._1 + "\\b"; // \u0008
								case '\f': return t._1 + "\\f"; // \u000C
								case '\n': return t._1 + "\\n"; // \u000A
								case '\r': return t._1 + "\\r"; // \u000D
								case '\t': return t._1 + "\\t"; // \u0009
								//case ',': return t._1 + "\\,"; // firefox parse error
	 */
	@Test
	public void test() {
		Map<String, Object> test = new LinkedHashMap<>();
		String expected = "Ausschluss \"Test\" Ausschluss \"Test\" Geschenk \"Test\" \"\" \'  Test   \'  \\' \\ / \b \f \n \r \t";
		test.put("Stichwörter", expected);
		String json;
		try {
			json = Json.stringify(test, 2);
			System.out.println(json);		
			Object o = Json.parseStrict(json);
			assertTrue(o instanceof Map);
			
			Map<String, Object> result = Athanor.cloneMap((Map<?,?>)o);
			assertEquals(expected, result.get("Stichwörter"));
			
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		
		
	}
	
	@Test
	public void test2() {
		Map<String, Object> test = new LinkedHashMap<>();
		String expected = "\"<1,2,3>Nur ein Quote Zeichen";
		test.put("Stichwörter", expected);
		String json;
		try {
			json = Json.stringify(test, 2);
			System.out.println(json);		
			Object o = Json.parseStrict(json);
			assertTrue(o instanceof Map);
			
			Map<String, Object> result = Athanor.cloneMap((Map<?,?>)o);
			assertEquals(expected, result.get("Stichwörter"));
			
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		
		
	}
	
	public static class TestObj {
		@Attr("Stichwörter") public String getStichwoerter() {
			return "\"<1,2,3>Nur ein Quote Zeichen";
		}
	}
	@Test
	public void test3() {
		TestObj test = new TestObj();
		String expected = "\"<1,2,3>Nur ein Quote Zeichen";
		String json;
		try {
			json = Json.stringify(test, 2);
			System.out.println(json);		
			Object o = Json.parseStrict(json);
			assertTrue(o instanceof Map);
			
			Map<String, Object> result = Athanor.cloneMap((Map<?,?>)o);
			assertEquals(expected, result.get("Stichwörter"));
			
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		
		
	}

}
