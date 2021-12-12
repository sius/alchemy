package liquer.alchemy.athanor.reflect;

import liquer.alchemy.athanor.TypedMap;
import liquer.alchemy.athanor.json.Json;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AthanorTest {


    @Test
	void test() throws NoSuchMethodException, InvocationTargetException {
		Class<?> type = ArrayList.class;
		try {
			@SuppressWarnings({"rawtypes", "unchecked"})
			List<String> test = (List) type.getDeclaredConstructor().newInstance();
			test.add("test");
			assertEquals(1, test.size());
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}

	}

	// FIXME
	@Test
	@Disabled
	void sequences() {
		String data1 = "[0, 1, 2]";
		String data2 = "[3, 4]";
		String data3 = "[5]";
		Integer[] testArray0 =  Json.assign(new Integer[0], data1, data2, data3);
		assertNotNull(testArray0);
		assertEquals(6, testArray0.length);
		assertEquals((Integer)0, testArray0[0]);
		assertEquals((Integer)1, testArray0[1]);
		assertEquals((Integer)2, testArray0[2]);
		assertEquals((Integer)3, testArray0[3]);
		assertEquals((Integer)4, testArray0[4]);
		assertEquals((Integer)5, testArray0[5]);

		ArrayList<Integer> testList0 =  Json.assign(new ArrayList<>(), data1, data2, data3);
		assertNotNull(testList0);
		assertEquals(6, testList0.size());
		assertEquals((Double)0d, testList0.get(0));
		assertEquals((Double)1d, testList0.get(1));
		assertEquals((Double)2d, testList0.get(2));
		assertEquals((Double)3d, testList0.get(3));
		assertEquals((Double)4d, testList0.get(4));
		assertEquals((Double)5d, testList0.get(5));

		//Stream<Integer> testList1 = Json.assign(LiquerStream.<Integer>stream(Optional.<Integer>empty()), Json.stringify(testArray0));

		//assertEquals((Integer)15, testList1.reduce(0, (a, b) -> a + b));
		//assertEquals(testList0.size(), testList1.count());


		int[] test1 =  Json.assign(new int[0], data1, data2);
		assertNotNull(test1);
		assertEquals(5, test1.length);
		assertEquals(0, test1[0]);
		assertEquals(1, test1[1]);
		assertEquals(2, test1[2]);
		assertEquals(3, test1[3]);
		assertEquals(4, test1[4]);

		String[] test2 =  Json.assign(new String[0], data1, data2);
		assertNotNull(test2);
		assertEquals(5, test2.length);
		assertEquals("0.0", test2[0]);
		assertEquals("1.0", test2[1]);
		assertEquals("2.0", test2[2]);
		assertEquals("3.0", test2[3]);
		assertEquals("4.0", test2[4]);

		long[] test3 = Json.assign(new long[0], data1, data2);
		assertNotNull(test3);
		assertEquals(5, test3.length);
		assertEquals(0L, test3[0]);
		assertEquals(1L, test3[1]);
		assertEquals(2L, test3[2]);
		assertEquals(3L, test3[3]);
		assertEquals(4L, test3[4]);

		data1 = "[\"Hello\",\"World\",\"!\"]";
		data2 = "[\"Lirum\",\"Larum\"]";
		String[] test4 = Json.assign(new String[3], data1, data2);
		assertNotNull(test4);
		assertEquals(8, test4.length);
		assertNull(test4[0]);
		assertNull(test4[1]);
		assertNull(test4[2]);
		assertEquals("Hello", test4[3]);
		assertEquals("World", test4[4]);
		assertEquals("!", test4[5]);
		assertEquals("Lirum", test4[6]);
		assertEquals("Larum", test4[7]);

		data1 = "[\tnull,\t\n\n1,\r\r\r2, 3]";
		Integer[] test5 =  Json.assign(new Integer[0], data1);
		assertNotNull(test5);
		assertEquals(4, test5.length);
		assertNull(test5[0]);
		assertEquals((Integer)1, test5[1]);
		assertEquals((Integer)2, test5[2]);
		assertEquals((Integer)3, test5[3]);

		int[] test6 =  Json.assign(new int[0], data1);
		assertNotNull(test6);
		assertEquals(4, test6.length);
		assertEquals(0, test6[0]);
		assertEquals(1, test6[1]);
		assertEquals(2, test6[2]);
		assertEquals(3, test6[3]);

		data1 = "[true,  false,    3.14,    null,  	\"Hello\",    \"World\",      \"!\",	true]";
		Object[] test7 = Json.assign(new Object[0], data1);
		assertNotNull(test7);
		assertEquals(8, test7.length);
		assertEquals((Boolean)true, test7[0]);
		assertEquals((Boolean)false, test7[1]);
		assertEquals((Double)3.14, test7[2]);
		assertNull(test7[3]);
		assertEquals("Hello", test7[4]);
		assertEquals("World", test7[5]);
		assertEquals("!", test7[6]);
		assertEquals((Boolean)true, test7[7]);
	}

	// FIXME
	@Test
	@Disabled
	void booleanSequences() {
		String data = "[ null, false, true, false, true]";
		boolean[] test8 = Json.assign(new boolean[0], data);
		assertNotNull(test8);
		assertEquals(5, test8.length);
		assertEquals(false, test8[0]);
		assertEquals(false, test8[1]);
		assertEquals(true, test8[2]);
		assertEquals(false, test8[3]);
		assertEquals(true, test8[4]);

		Boolean[] test9 = Json.assign(new Boolean[0], data);
		assertNotNull(test9);
		assertEquals(5, test9.length);
		assertNull(test9[0]);
		assertEquals(false, test9[1]);
		assertEquals(true, test9[2]);
		assertEquals(false, test9[3]);
		assertEquals(true, test9[4]);
	}
	@Test
	void emptyObj() {
		String data = "{}";
		TestContract test =  Json.assign(TestContract.class, data);
		assertNotNull(test);
	}
	@Test
	void updateContractValue() {
		String data = "{\"IntValue\":200}";
		TestContract test = new TestContract();
		test.setIntValue(199);
		assertEquals(199, test.getIntValue());
		try {
			test =  Json.assignStrict(test, data);
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		assertEquals(200, test.getIntValue());
	}
	@Test
	void createContract() {
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		String nowStr = Json.Singleton.CONFIG.getSimpleDateFormat().format(now);
		String data =
			"{" +
			"\"IntArray\": [null, null]," +
			"\"IntegerList\": [null, 0]," +
			"\"PrimitiveBooleanValue\":true," +
			"\"BooleanValue\":true," +
			"\"PrimitiveByteValue\":127," +
			"\"ByteValue\":128," +
			"\"PrimitiveShortValue\":12099," +
			"\"ShortValue\":12100," +
			"\"IntValue\":99," +
			"\"IntegerValue\":100," +
			"\"PrimitiveLongValue\":199.0," +
			"\"LongValue\":200.0," +
			"\"PrimitiveFloatValue\":3.1415," +
			"\"FloatValue\":2.73," +
			"\"PrimitiveDoubleValue\":0.3333333," +
			"\"DoubleValue\":0.1897865," +
			"\"StringValue\":\"Hello World\"," +
			"\"DateValue\":\""+ nowStr+"\"," +
			"\"BigDecimalValue\":\"0.0000000000001515152998767\"," +
			"\"BigIntegerValue\":\"123456789015151523456298765748\"," +
			"\"NestedContract\": {" +
				"\"DateValue\":\""+nowStr+"\"," +
				"\"IntArray\": [0,1,2,3]," +
				"\"IntegerList\": [0,1,2,3]" +
			"}}";


		TestContract test = null;
		try {
			test = Json.assignStrict(TestContract.class, data);
		} catch (ParseException e) {
			fail(e.getMessage());
		}

		assertNotNull(test);

		assertEquals(true, test.isPrimitiveBooleanValue());
		assertEquals(Boolean.TRUE, test.getBooleanValue());

		assertEquals((byte)127, test.getPrimitiveByteValue());
		assertEquals((Byte)(byte)128, test.getByteValue());

		assertEquals((short)12099, test.getPrimitiveShortValue());
		assertEquals((Short)(short)12100, test.getShortValue());

		assertEquals(99, test.getIntValue());
		assertEquals((Integer)100, test.getIntegerValue());

		assertEquals(199L, test.getPrimitiveLongValue());
		assertEquals((Long)200L, test.getLongValue());

		assertEquals(3.1415f, test.getPrimitiveFloatValue(), 0.0005);
		assertEquals((Float)2.73f, test.getFloatValue());

		assertEquals(0.3333333, test.getPrimitiveDoubleValue(), 0.00000005);
		assertEquals((Double)0.1897865, test.getDoubleValue());

		assertEquals("Hello World", test.getStringValue());
		assertEquals(now, test.getDateValue());

		assertEquals(new BigDecimal("0.0000000000001515152998767"), test.getBigDecimalValue());
		assertEquals(new BigInteger("123456789015151523456298765748"), test.getBigIntegerValue());

		assertNotNull(test.getNestedContract());
		assertEquals(now, test.getNestedContract().getDateValue());
		assertNotNull(test.getNestedContract().getIntArray());
		assertEquals(4, test.getNestedContract().getIntArray().length);
		assertEquals(0, test.getNestedContract().getIntArray()[0]);
		assertEquals(1, test.getNestedContract().getIntArray()[1]);
		assertEquals(2, test.getNestedContract().getIntArray()[2]);
		assertEquals(3, test.getNestedContract().getIntArray()[3]);
		List<Integer> integerList =  test.getNestedContract().getIntegerList();
		assertNotNull(integerList);
		assertEquals(4, integerList.size());
		assertEquals((Integer)0, integerList.get(0));
		assertEquals((Integer)1, integerList.get(1));
		assertEquals((Integer)2, integerList.get(2));
		assertEquals((Integer)3, integerList.get(3));
	}

	@Test
	void createMap() {
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		String nowStr = Json.Singleton.CONFIG.getSimpleDateFormat().format(now);
		String data =
			"{" +
			"\"PrimitiveBooleanValue\":true," +
			"\"BooleanValue\":true," +
			"\"PrimitiveByteValue\":127," +
			"\"ByteValue\":128," +
			"\"PrimitiveShortValue\":12099," +
			"\"ShortValue\":12100," +
			"\"IntValue\":99," +
			"\"IntegerValue\":100," +
			"\"PrimitiveLongValue\":199.0," +
			"\"LongValue\":200.0," +
			"\"PrimitiveFloatValue\":3.1415," +
			"\"FloatValue\":2.73," +
			"\"PrimitiveDoubleValue\":0.3333333," +
			"\"DoubleValue\":0.1897865," +
			"\"StringValue\":\"Hello World\"," +
			"\"DateValue\":\""+nowStr+"\"," +
			"\"BigDecimalValue\":\"0.0000000000001515152998767\"," +
			"\"BigIntegerValue\":\"123456789015151523456298765748\"" +
			"}";

		TypedMap test = new TypedMap();
		test =  Json.assign(test, data);
		assertNotNull(test);

		assertEquals(true, test.booleanValue("PrimitiveBooleanValue"));
		assertEquals(Boolean.TRUE, test.booleanValue("BooleanValue"));

		assertEquals((byte)127, test.integerValue("PrimitiveByteValue").byteValue());
		assertEquals((byte)128, test.integerValue("ByteValue").byteValue());

		assertEquals((short)12099, test.shortValue("PrimitiveShortValue").shortValue());
		assertEquals((Double)12100d, test.doubleValue("ShortValue"));

		assertEquals(99, (int)test.integerValue("IntValue"));
		assertEquals((Integer)100, test.integerValue("IntegerValue"));

		assertEquals(199l, test.longValue("PrimitiveLongValue").longValue());
		assertEquals((Long)200l, test.longValue("LongValue"));

		assertEquals((Float)3.1415f, test.floatValue("PrimitiveFloatValue"));
		assertEquals((Float)2.73f, test.floatValue("FloatValue"));

		assertEquals((Double)0.3333333d, test.doubleValue("PrimitiveDoubleValue"));
		assertEquals((Double)0.1897865, test.doubleValue("DoubleValue"));

		assertEquals("Hello World", test.get("StringValue"));
		assertEquals(Json.Singleton.CONFIG.getSimpleDateFormat().format(now), test.get("DateValue"));

		assertEquals("0.0000000000001515152998767", test.get("BigDecimalValue"));
		assertEquals("123456789015151523456298765748", test.get("BigIntegerValue"));

	}
}
