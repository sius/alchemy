package liquer.alchemy;

import liquer.alchemy.athanor.Attr;
import liquer.alchemy.json.Json;
import liquer.alchemy.json.JsonIndentWriter;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class JsonTest {
	@Test
	public void types() {
		assertEquals("\"2014-01-07T14:28:14.000+00:00\"", Json.stringify(new Date(1389104894000L)));
		assertEquals("null", Json.Singleton.NULL.toString());
		assertEquals("null", Json.stringify(null));
		assertEquals("\"Überraschung\"", new Json.JStr("Überraschung").toString());
		assertEquals("\"Überraschung\"", Json.stringify("Überraschung"));
		assertEquals("\"Ü\"", Json.stringify('Ü'));
		assertEquals("\"Ü\"", Json.stringify('Ü'));
		assertEquals("\"Ü\"", Json.stringify('Ü'));
		assertEquals("true", Json.Singleton.TRUE.toString());
		assertEquals("true", Json.stringify(true));
		assertEquals("false", Json.Singleton.FALSE.toString());
		assertEquals("false", Json.stringify(false));
		assertEquals("3.0", new Json.JNum(3D).toString());
		assertEquals("3.0", Json.stringify(3));
		//converts long to double
		assertEquals("3.0", new Json.JNum(3L).toString());
		//converts long to String; preserves precision of the long
		assertEquals("\"3\"", Json.stringify(3L));
		assertEquals("3.14", Json.stringify(3.14));
		//converts BigInteger to String; preserves precision of the BigInteger
		assertEquals("\"3\"", Json.stringify(new BigInteger("3")));
		//converts BigDecimal to String; precision lost because of float argument in constructor
		assertEquals("\"3.1400001049041748046875\"", Json.stringify(new BigDecimal(3.14F)));
		//assertEquals("\"3.140000104904175\"", Json.mkstr(new BigDecimal(3.14F)));
		assertEquals("\"3.140000000000000124344978758017532527446746826171875\"", Json.stringify(new BigDecimal(3.14)));
		assertEquals("3.14", new Json.JNum(new BigDecimal(3.14).doubleValue()).toString());
		
		assertEquals("3.140000104904175", new Json.JNum(3.14F).toString());
		assertEquals("3.140000104904175", Json.stringify(3.14F));
		assertEquals("255.0", new Json.JNum((short)255).toString());
		assertEquals("255.0", Json.stringify((short)255));
		assertEquals("127.0", new Json.JNum((byte)127).toString());
		assertEquals("127.0", Json.stringify((byte)127));
		assertEquals("-1.0", new Json.JNum((byte)255).toString());
		assertEquals("-1.0", Json.stringify((byte)255));
	}
	@Test
	public void arrays() {
		assertEquals("[\"one\",\"two\",\"three\"]", Json.stringify(new String[] {"one", "two", "three"}));
		assertEquals("[1.0,2.0,3.0]", Json.stringify(new int[] {1, 2, 3}));
		assertEquals("[1.0,2.0,3.0]", Json.stringify(new Integer[] {1, 2, 3}));
		assertEquals("[1.0,1.5,1.75]", Json.stringify(new double[] {1.0, 1.5, 1.75}));
		assertEquals("[true,true,false,false,true]", Json.stringify(new boolean[] {true, true, false, false, true}));
	}
	@Test
	public void iterables() {
		List<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");
		list.add("three");
		
		assertEquals("[\"one\",\"two\",\"three\"]", Json.stringify(list));
		assertEquals("[\"one\",\"two\",\"three\"]", Json.stringify(list.iterator()));
		
		List<Double> listD = new ArrayList<>();
		listD.add(1.0);
		listD.add(1.5);
		listD.add(1.75);
		
		assertEquals("[1.0,1.5,1.75]", Json.stringify(listD));
		assertEquals("[1.0,1.5,1.75]", Json.stringify(listD.iterator()));
	}

	class SomeObject {
		@Attr
		public String name = "Something - ÄÖÜß";
		@Attr public int myInt = 5;
		@Attr public boolean myBool = true;
		@Attr("theTitle") public String getTitle() { return name; }
		@Attr public OtherObject myOther = new OtherObject();

		public final String EXPECTED = "{" +
			"\"theTitle\":\"Something - ÄÖÜß\"," +
			"\"name\":\"Something - ÄÖÜß\"," +
			"\"myBool\":true," +
			"\"myInt\":5.0," +
			"\"myOther\":" +
			"{\"otherInt\":5.0," +
			"\"someDoubles\":[1.0,2.0,500.77]," +
			"\"otherBool\":true," +
			"\"title\":\"Other\"}}";
	}
	class OtherObject {
		@Attr public String title = "Other";
		@Attr public int otherInt = 5;
		@Attr public boolean otherBool = true;
		@Attr public double[] someDoubles = new double[]{1.0, 2.0, 500.77};
		public int[] someInts = new int[]{1, 2, 500};
	}
	@Test
	public void stringify() {
		SomeObject some = new SomeObject();
		Object expected = Json.parse(some.EXPECTED);
		Object actual = Json.parse(Json.stringify(some));
		assertTrue(expected instanceof Map);
		assertTrue(actual instanceof Map);
		assertMapEquals((Map<?,?>)expected, (Map<?,?>)actual);
		
	}
	class NumberObject {
		@Attr public int maxInt = Integer.MAX_VALUE;
		@Attr public int minInt = Integer.MIN_VALUE;
		@Attr public long maxLong = Long.MAX_VALUE;
		@Attr public long minLong = Long.MIN_VALUE;
		@Attr public BigDecimal posBigDec = new BigDecimal("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
		@Attr public BigDecimal negBigDec = new BigDecimal("-12345678901234567890123456789012345678901234567890123456789012345678901234567890");
		@Attr public BigInteger posBigInt = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
		@Attr public BigInteger negBigInt = new BigInteger("-12345678901234567890123456789012345678901234567890123456789012345678901234567890");
	}
	@Test
	public void number() {
		Json.writeTo(Json.obj(new NumberObject()), new JsonIndentWriter(System.out));
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
	
	public void parseStrict() throws IOException {
		Object o;
		try {
			o = Json.parseStrict(getClass().getResourceAsStream("resources.json"));
			assertNotNull(o);
			
			assertTrue(o instanceof Collection);
			Collection<?> resources = (Collection<?>)o;
			
			for (Object resourceObj : resources) {
				assertTrue(resourceObj instanceof Map);
				Map<?, ?> resource = (Map<?, ?>)resourceObj;
				assertEquals(1, resource.size());
				Iterator<?> iter = resource.entrySet().iterator();
				o = iter.next();
				assertTrue(o instanceof Entry);
				Entry<?, ?> entry = (Entry<?, ?>)o;
				Object resourceName = entry.getKey();
				o = entry.getValue();
				assertTrue(o instanceof Map);
				Map<?, ?> resourceMap = (Map<?, ?>)o;
				o = resourceMap.get("routes");
				assertTrue(o instanceof Collection);
				Collection<?> routes = (Collection<?>)o;
				assertTrue(routes.size() > 0);
				for (Object routeObj : routes) {
					assertTrue(routeObj instanceof Map);
					Map<?,?> route = (Map<?,?>)routeObj;
					assertTrue(route.containsKey("match"));
					assertTrue(route.get("match") instanceof String);
					assertTrue(route.containsKey("method"));
					assertTrue(route.get("method") instanceof Collection);
					assertTrue(route.containsKey("route"));
					assertTrue(route.get("route") instanceof String);
					assertTrue(route.containsKey("binding"));
					assertTrue(route.get("binding") instanceof Map);
					
					Map<?,?> binding = (Map<?,?>)route.get("binding");
					assertTrue(binding.get(":controller") instanceof String);
					assertEquals(binding.get(":controller"), resourceName);
					assertTrue(binding.get(":action") instanceof String);
				}
			}
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}
}
