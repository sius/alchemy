package liquer.alchemy;

import liquer.alchemy.athanor.Athanor;
import liquer.alchemy.athanor.Attr;
import liquer.alchemy.json.Json;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class JsonReaderTest {

	@Test
	public void strSeq() {
		assertJson(Arrays.asList("Larry", "Moe", "Curley"), "[\"Larry\",\"Moe\",\"Curley\"]");
	}
	
	@Test
	public void numSeq() {
		List<Double> expected = new ArrayList<>();
		for (int i = 0; i < 10000; i++ ) expected.add((double) i);
		String actual = expected.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
		assertJson(expected, actual);
	}
	
	@Test
	public void objSeq() {
		assertJson(Arrays.<Object>asList("Larry", "Moe","Curley", "", 3.14, true, false, null), "[\"Larry\",\"Moe\",\"Curley\",\"\",3.14,true,false,null]");
	}
	
	@Test
	public void escapedSeq() {
		assertJson(Arrays.asList("\"", "\\", "/", "\b", "\f", "\n", "\r", "\t"), "[\"\\\"\",\"\\\\\",\"\\/\",\"\\b\",\"\\f\",\"\\n\",\"\\r\",\"\\t\"]");
		assertJson(Arrays.asList("©","© DATEV eG."), "[\"\u00A9\",\"\u00A9 DATEV eG.\"]");
	}
	
	@Test
	public void nestedSeq() {
		String[][] expected = new String[][] {
			{ "Pferd", "Schaf", "Hund" },
			{ "rot", "gelb", "blau" },
			{ "Auto", "Rad", "Zug" },
		};
		String actual = Json.stringify(expected);
		assertEquals("[[\"Pferd\",\"Schaf\",\"Hund\"],[\"rot\",\"gelb\",\"blau\"],[\"Auto\",\"Rad\",\"Zug\"]]", actual);
		
	}
	public static class Animal {
		@Attr private String name;
		@Attr private int legs;
		
		public Animal() {
			
		}
		public Animal(String name, int legs) {
			this.name = name;
			this.legs = legs;
		}
		@Attr("name") public String getName() {
			return name;
		}
		@Attr("legs") public int getLegs() {
			return legs;
		}
	}
	@Test
	public void animalSeq() {
		List<Animal> animals = new ArrayList<>();
		animals.add(new Animal("dog", 4));
		animals.add(new Animal("cat", 4));
		animals.add(new Animal("horse", 4));
		animals.add(new Animal("bird", 2));
		animals.add(new Animal("snake", 0));
		String expected = "[{\"name\":\"dog\",\"legs\":4.0},{\"name\":\"cat\",\"legs\":4.0},{\"name\":\"horse\",\"legs\":4.0},{\"name\":\"bird\",\"legs\":2.0},{\"name\":\"snake\",\"legs\":0.0}]";
		
		Animal[] expectedAnimals = Json.assign(new Animal[0], expected);
		System.out.println(Json.stringify(expectedAnimals));
		System.out.println(Json.stringify(animals));
		assertEquals(Json.stringify(expectedAnimals), Json.stringify(animals));
		
	}

	@Test
	public void parseAnimalSeq() {
		List<Animal> animals = new ArrayList<>();
		animals.add(new Animal("dog", 4));
		animals.add(new Animal("cat", 4));
		animals.add(new Animal("horse", 4));
		animals.add(new Animal("bird", 2));
		animals.add(new Animal("snake", 0));
		String expected = "[{\"name\":\"dog\",\"legs\":4.0},{\"name\":\"cat\",\"legs\":4.0},{\"name\":\"horse\",\"legs\":4.0},{\"name\":\"bird\",\"legs\":2.0},{\"name\":\"snake\",\"legs\":0.0}]";
		try {
			Object o = Json.parseStrict(expected);
			assertTrue(o instanceof List);
			List<?> list = (List<?>)o;
			for (Object item : list) {
				assertTrue (item instanceof Map);
				Map<String, Object> map = Athanor.cloneMap((Map<?, ?>)item);
				assertTrue(map.containsKey("name"));
				assertTrue(map.containsKey("legs"));
				assertTrue(map.get("name") instanceof String);
				assertTrue(map.get("legs") instanceof Double);
			}
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void emptySeq() {
		assertJson(new ArrayList<String>(), "[]");
	}
	
	@Test
	public void emptyObj() {
		try {
			assertEquals(new HashMap<String, Json>(), Json.parseStrict("{}"));
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}

	public void assertJson(Map<String, Object> expected, String actual) {
		assertNotNull(expected);
		assertNotNull(actual);

		Object result;
		try {
			result = Json.parseStrict(actual);
			assertTrue(result instanceof Map);
			Map<?, ?> act = (Map<?,?>)result;
			assertMapEquals(expected, act);
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}

	public void assertJson(List<?> expected, String actual) {
		assertNotNull(expected);
		assertNotNull(actual);

		Object result;
		try {
			result = Json.parseStrict(actual);
			assertTrue(result instanceof List);
			List<?> act = (List<?>)result;
			assertListEquals(expected, act);
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}
	public void assertListEquals(List<?> expected, List<?> actual) {
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < actual.size(); i++) {
			Object exp = expected.get(i);
			Object act = actual.get(i);
			if (act instanceof List) {
				if (exp instanceof List) {
					assertListEquals((List<?>)exp, (List<?>)act);
				} else {
					fail("item " + i + " of expected is not a List type");
				}
			} else if (act instanceof Map) {
				if (exp instanceof Map) {
					assertMapEquals((Map<?,?>)exp, (Map<?,?>)act);
				} else {
					fail("item " + i + " of expected is not a Map type");
				}
			} else {
				assertEquals(exp, act);
			}
		}
	}
	public void assertMapEquals(Map<?,?> expected, Map<?,?> actual) {
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for (Entry<?, ?> entry : expected.entrySet()) {
			assertTrue(actual.containsKey(entry.getKey()));
			Object exp = entry.getValue();
			Object act = actual.get(entry.getKey());
			if (act instanceof List) {
				if (exp instanceof List) {
					assertListEquals((List<?>)exp, (List<?>)act);
				} else
					fail("entry value " + entry.getKey() + " of expected is not a List type");
			} else if (act instanceof Map) {
				if (exp instanceof Map) {
					assertMapEquals((Map<?,?>)exp, (Map<?,?>)act);
				} else
					fail("entry value " + entry.getKey() + " of expected is not a Map type");
			} else {
				assertEquals(exp, act);
			}
		}
	}
}
