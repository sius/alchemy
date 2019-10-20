package liquer.alchemy.athanor.json;

import liquer.alchemy.athanor.reflect.Athanor;
import liquer.alchemy.athanor.reflect.Attr;
import liquer.alchemy.athanor.json.Json;
import org.junit.Test;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonWriteTest {

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
