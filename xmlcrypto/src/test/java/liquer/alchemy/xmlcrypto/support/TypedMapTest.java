package liquer.alchemy.xmlcrypto.support;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class TypedMapTest {

    private static TypedMap TM_VALUES;
    private enum TestEnum { A, B, C };


    @BeforeClass
    public static void init() {
        TM_VALUES = new TypedMap(YashMap.of(
            "Boolean", Boolean.TRUE,
            "Short", Short.MAX_VALUE,
            "Integer", Integer.MAX_VALUE,
            "Float", Float.MAX_VALUE,
            "Double", Double.MAX_VALUE,
            "Long", Long.MAX_VALUE,
            "String", "Hello World",
            "Enum", TestEnum.A));
    }
    @Test
    public void testTryGetValue() {

        Assert.assertTrue(Boolean.TRUE == TM_VALUES.tryBooleanValue("Boolean").get());
        Assert.assertTrue(Short.MAX_VALUE == TM_VALUES.tryShortValue("Short").get());
        Assert.assertTrue(Integer.MAX_VALUE == TM_VALUES.tryIntegerValue("Integer").get());
        Assert.assertTrue(Float.MAX_VALUE == TM_VALUES.tryFloatValue("Float").get());
        Assert.assertTrue(Double.MAX_VALUE == TM_VALUES.tryDoubleValue("Double").get());
        Assert.assertTrue(Long.MAX_VALUE == TM_VALUES.tryLongValue("Long").get());
        Assert.assertTrue("Hello World".equals(TM_VALUES.tryStringValue("String").get()));
        Assert.assertTrue(TestEnum.A.equals(TM_VALUES.tryEnumValue(TestEnum.class, "Enum").get()));
    }

    @Test
    public void testTryGetListValue() {
        List<Boolean> expectedBooleans = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
        List<Short> expectedShorts = Arrays.asList(Short.MAX_VALUE, Short.MIN_VALUE);
        List<Integer> expectedIntegers = Arrays.asList(Integer.MAX_VALUE,Integer.MIN_VALUE);
        List<Float> expectedFloats = Arrays.asList(Float.MAX_VALUE, Float.MIN_VALUE);
        List<Double> expectedDoubles = Arrays.asList(Double.MAX_VALUE, Double.MIN_VALUE);
        List<Long> expectedLongs =  Arrays.asList(Long.MAX_VALUE, Long.MIN_VALUE);
        List<String> expectedStrings = Arrays.asList("Hello World", "Test");

        TypedMap tm = new TypedMap(YashMap.of(
            "Boolean", expectedBooleans,
            "Short", expectedShorts,
            "Integer", expectedIntegers,
            "Float", expectedFloats,
            "Double", expectedDoubles,
            "Long", expectedLongs,
            "String", expectedStrings));

        Assert.assertArrayEquals(expectedBooleans.toArray(),
            tm.tryBooleanList("Boolean").get().toArray());
        Assert.assertArrayEquals(expectedShorts.toArray(),
            tm.tryShortList("Short").get().toArray());
        Assert.assertArrayEquals(expectedIntegers.toArray(),
            tm.tryIntegerList("Integer").get().toArray());
        Assert.assertArrayEquals(expectedFloats.toArray(),
            tm.tryFloatList("Float").get().toArray());
        Assert.assertArrayEquals(expectedDoubles.toArray(),
            tm.tryDoubleList("Double").get().toArray());
        Assert.assertArrayEquals(expectedLongs.toArray(),
            tm.tryLongList("Long").get().toArray());
        Assert.assertArrayEquals(expectedStrings.toArray(),
            tm.tryStringList("String").get().toArray());
    }

    @Test
    public void testTryGetArrayValue() {
        Boolean[] expectedBooleans = { Boolean.TRUE, Boolean.FALSE };
        Short[] expectedShorts = { Short.MAX_VALUE, Short.MIN_VALUE };
        Integer[] expectedIntegers = { Integer.MAX_VALUE,Integer.MIN_VALUE };
        Float[] expectedFloats = { Float.MAX_VALUE, Float.MIN_VALUE };
        Double[] expectedDoubles = { Double.MAX_VALUE, Double.MIN_VALUE };
        Long[] expectedLongs = { Long.MAX_VALUE, Long.MIN_VALUE };
        String[] expectedStrings = { "Hello World", "Test" };

        TypedMap tm = new TypedMap(YashMap.of(
            "Boolean", expectedBooleans,
            "Short", expectedShorts,
            "Integer", expectedIntegers,
            "Float", expectedFloats,
            "Double", expectedDoubles,
            "Long", expectedLongs,
            "String", expectedStrings));

        Assert.assertArrayEquals(expectedBooleans,
            tm.tryBooleanArray("Boolean").get());
        Assert.assertArrayEquals(expectedShorts,
            tm.tryShortArray("Short").get());
        Assert.assertArrayEquals(expectedIntegers,
            tm.tryIntegerArray("Integer").get());
        Assert.assertArrayEquals(expectedFloats,
            tm.tryFloatArray("Float").get());
        Assert.assertArrayEquals(expectedDoubles,
            tm.tryDoubleArray("Double").get());
        Assert.assertArrayEquals(expectedLongs,
            tm.tryLongArray("Long").get());
        Assert.assertArrayEquals(expectedStrings,
            tm.tryStringArray("String").get());
    }

    @Test
    public void testPrune() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        final TypedMap ptm = tm.prune("1", "2", "100");
        Assert.assertEquals("1", tm.get("1"));
        Assert.assertEquals(null, tm.get("100"));
        Assert.assertEquals(3, tm.size());
        Assert.assertEquals(2, ptm.size());
        Assert.assertEquals(null, ptm.get("0"));
        Assert.assertEquals("1", ptm.get("1"));
        Assert.assertEquals("2", ptm.get("2"));
        Assert.assertEquals(null, ptm.get("100"));
    }

    @Test
    public void testSelfPrune() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        Assert.assertEquals("0", tm.get("0"));
        Assert.assertEquals("1", tm.get("1"));
        Assert.assertEquals("2", tm.get("2"));
        Assert.assertEquals(null, tm.get("100"));
        final TypedMap ptm = tm.selfPrune("1", "2", "100");

        Assert.assertEquals(2, ptm.size());
        Assert.assertEquals(null, ptm.get("0"));
        Assert.assertEquals("1", ptm.get("1"));
        Assert.assertEquals("2", ptm.get("2"));
        Assert.assertEquals(null, ptm.get("100"));

        Assert.assertTrue(tm == ptm);
    }

    @Test
    public void testPutWithObjectKey() {
        final TypedMap tm = new TypedMap();
        Object key = "key";
        Object value = "value";
        tm.putWithObjectKey(key, value);
        Assert.assertTrue(tm.containsKey(key.toString()));
        Assert.assertTrue(tm.containsValue(value.toString()));
        Assert.assertEquals("value", tm.get(key.toString()));
    }

    @Test
    public void testGet() {
        final TypedMap tm = new TypedMap();
        tm.put("0", "0");
        Assert.assertEquals("0", tm.get("0"));
        Assert.assertEquals(null, tm.get("1"));

    }

    @Test
    public void valueOf() {
    }

    @Test
    public void listOf() {
    }

    @Test
    public void testDeleteString() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        String actual = tm.deleteString("0");
        Assert.assertEquals("0", actual);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals("100", tm.deleteString("100", "100"));
        Assert.assertEquals(null, tm.deleteString("100"));
    }

    @Test
    public void testDeleteLong() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        long actual = tm.deleteLong("0");
        Assert.assertEquals(0L, actual);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals(100L, (long)tm.deleteLong("100", 100L));
        Assert.assertEquals(null, tm.deleteLong("100"));
    }

    @Test
    public void testDeleteInteger() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        int actual = tm.deleteInteger("0");
        Assert.assertEquals(0, actual);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals(100, (int)tm.deleteInteger("100", 100));
        Assert.assertEquals(null, tm.deleteInteger("100"));
    }

    @Test
    public void testDeleteShort() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        short actual = tm.deleteShort("0");
        Assert.assertEquals((short)0, actual);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals((short)100, (short)tm.deleteShort("100", (short)100));
        Assert.assertEquals(null, tm.deleteInteger("100"));
    }

    @Test
    public void testDeleteDouble() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        double actual = tm.deleteDouble("0");
        Assert.assertEquals(0.0, actual, 0);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals(100.0, (double)tm.deleteDouble("100", 100.0), 0);
        Assert.assertEquals("OK", null, tm.deleteDouble("100"));
    }

    @Test
    public void testDeleteFloat() {

        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        float actual = tm.deleteFloat("0");
        Assert.assertEquals(0.0f, actual, 0);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals(100.0f, (double)tm.deleteFloat("100", 100.0f), 0);
        Assert.assertEquals("OK", null, tm.deleteFloat("100"));
    }

    @Test
    public void testDeleteBoolean() {

        final TypedMap tm = new TypedMap(YashMap.of("0", true, "1", "1", "2", "2"));
        boolean actual = tm.deleteBoolean("0");
        Assert.assertEquals(true, actual);
        Assert.assertEquals(null, tm.get("0"));
        Assert.assertEquals(false, tm.deleteBoolean("100", false));
        Assert.assertEquals(null, tm.deleteBoolean("100"));
    }

    @Test
    public void testDeleteStrings() {
        final TypedMap tm = new TypedMap(YashMap.of("key", "a", "key", "b", "key", "c"));
        Assert.assertEquals(1, tm.size());
        final String[] actual = tm.deleteStrings("key");
        Assert.assertArrayEquals(new String[] {"a", "b", "c"}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteLongs() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1L, "key", 2L, "key", 3L));
        Assert.assertEquals(1, tm.size());
        final Long[] actual = tm.deleteLongs("key");
        Assert.assertArrayEquals(new Long[] {1L, 2L, 3L}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteIntegers() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1, "key", 2, "key", 3));
        Assert.assertEquals(1, tm.size());
        final Integer[] actual = tm.deleteIntegers("key");
        Assert.assertArrayEquals(new Integer[] {1, 2, 3}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteShorts() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1, "key", 2, "key", 3));
        Assert.assertEquals(1, tm.size());
        final Short[] actual = tm.deleteShorts("key");
        Assert.assertArrayEquals(new Short[] {1, 2, 3}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteDoubles() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1.0, "key", 2.0, "key", 3.0));
        Assert.assertEquals(1, tm.size());
        final Double[] actual = tm.deleteDoubles("key");
        Assert.assertArrayEquals(new Double[] {1.0, 2.0, 3.0}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteFloats() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1f, "key", 2f, "key", 3f));
        Assert.assertEquals(1, tm.size());
        final Float[] actual = tm.deleteFloats("key");
        Assert.assertArrayEquals(new Float[] {1f, 2f, 3f}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteBooleans() {
        final TypedMap tm = new TypedMap(YashMap.of("key", true, "key", false, "key", true));
        Assert.assertEquals(1, tm.size());
        final Boolean[] actual = tm.deleteBooleans("key");
        Assert.assertArrayEquals(new Boolean[] {true, false, true}, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteLongWithDefaultValue() {
        Assert.assertEquals(1L, (long)(new TypedMap()).deleteLong("key", 1L));
    }

    @Test
    public void testDeleteIntegerWithDefaultValue() {
        Assert.assertEquals(1, (int)(new TypedMap()).deleteInteger("key", 1));
    }

    @Test
    public void testDeleteShortWithDefaultValue() {
        Assert.assertEquals((short)1, (short)(new TypedMap()).deleteShort("key", (short)1));
    }

    @Test
    public void testDeleteDoubleWithDefaultValue() {
        Assert.assertEquals(1.0, (double)(new TypedMap()).deleteDouble("key", 1.0), 0.0);
    }

    @Test
    public void testDeleteFloatWithDefaultValue() {
        Assert.assertEquals(1.0f, (float)(new TypedMap()).deleteFloat("key", 1.0f), 0.0);
    }

    @Test
    public void testDeleteBooleanWithDefaultValue() {
        Assert.assertEquals(true, (new TypedMap()).deleteBoolean("key", true));
    }

    @Test
    public void testDeleteStringsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final String[] expected = new String[] {"a", "b", "c"};
        final String[] actual = tm.deleteStrings("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteLongsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Long[] expected = new Long[] {1L, 2L, 3L};
        final Long[] actual = tm.deleteLongs("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteIntegersWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Integer[] expected = new Integer[] {1, 2, 3};
        final Integer[] actual = tm.deleteIntegers("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteShortsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Short[] expected = new Short[] {1, 2, 3};
        final Short[] actual = tm.deleteShorts("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteDoublesWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Double[] expected = new Double[] {1.0, 2.0, 3.0};
        final Double[] actual = tm.deleteDoubles("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteFloatsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Float[] expected = new Float[] {1f, 2f, 3f};
        final Float[] actual = tm.deleteFloats("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testDeleteBooleansWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        Assert.assertEquals(0, tm.size());
        final Boolean[] expected = new Boolean[] {true, false, true};
        final Boolean[] actual = tm.deleteBooleans("key", expected);
        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(0, tm.size());
    }

    @Test
    public void testStringValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1));
        final String actual = tm.stringValue("key");
        Assert.assertEquals("1", actual);
    }

    @Test
    public void tryStringValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", "test"));
        String actual = tm.tryStringValue("key").get();
        Assert.assertEquals("test", actual);
        try {
            tm.tryStringValue("key1").get();
        } catch (NoSuchElementException e) {
            Assert.assertNotNull(e);
        }

        try {
            tm.tryStringValue("key1")
                .orElseThrow(() -> new Exception("test"));
        } catch (Exception e) {
            Assert.assertEquals("test", e.getMessage());
        }

        actual = tm.tryStringValue("key1").orElse("hello");
        Assert.assertEquals("hello", actual);
        actual = tm.tryStringValue("key1").orElseGet(()->null);
        Assert.assertEquals(null, actual);


    }


    @Test
    public void testEnumValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", TestEnum.A, "key2", 1));
        final TestEnum actual = tm.enumValue(TestEnum.class, "key");
        Assert.assertEquals(TestEnum.A, actual);
    }

    @Test
    public void testLongValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Long.MAX_VALUE));
        final long actual = tm.longValue("key");
        Assert.assertEquals(Long.MAX_VALUE, actual);
    }

    @Test
    public void testIntegerValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Integer.MAX_VALUE));
        final int actual = tm.integerValue("key");
        Assert.assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    public void testShortValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Short.MAX_VALUE));
        final short actual = tm.shortValue("key");
        Assert.assertEquals(Short.MAX_VALUE, actual);
    }

    @Test
    public void testDoubleValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Double.MAX_VALUE));
        final double actual = tm.doubleValue("key");
        Assert.assertEquals(Double.MAX_VALUE, actual, 0d);
    }

    @Test
    public void testFloatValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Float.MAX_VALUE));
        final float actual = tm.floatValue("key");
        Assert.assertEquals(Float.MAX_VALUE, actual, 0f);
    }

    @Test
    public void testBooleanValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Boolean.TRUE));
        final boolean actual = tm.booleanValue("key");
        Assert.assertEquals(Boolean.TRUE, actual);
    }

    @Test
    public void availableKeys() {
        final TypedMap tm = new TypedMap(YashMap.of("a", 1, "b", 2, "c", 3));
        String actual = tm.availableKeys("0", "1", "a", "f", "b", "d", "e").collect(Collectors.joining(","));
        Assert.assertEquals("a,b", actual);
    }

    @Test
    public void testStringValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final String actual = tm.stringValue("key", "test");
        Assert.assertEquals("test", actual);
    }

    @Test
    public void testEnumValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final TestEnum actual = tm.enumValue(TestEnum.class, "key", TestEnum.A);
        Assert.assertEquals(TestEnum.A, actual);
    }

    @Test
    public void testLongValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final long actual = tm.longValue("key", Long.MAX_VALUE);
        Assert.assertEquals(Long.MAX_VALUE, actual);
    }

    @Test
    public void testIntegerValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final int actual = tm.integerValue("key", Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    public void testShortValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final short actual = tm.shortValue("key", Short.MAX_VALUE);
        Assert.assertEquals(Short.MAX_VALUE, actual);
    }

    @Test
    public void testDoubleValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final double actual = tm.doubleValue("key", Double.MAX_VALUE);
        Assert.assertEquals(Double.MAX_VALUE, actual, 0d);
    }

    @Test
    public void testFloatValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final float actual = tm.floatValue("key", Float.MAX_VALUE);
        Assert.assertEquals(Float.MAX_VALUE, actual,0f);
    }

    @Test
    public void testBooleanValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final boolean actual = tm.booleanValue("key", Boolean.TRUE);
        Assert.assertEquals(Boolean.TRUE, actual);
    }

    @Test
    public void stringArray() {
    }

    @Test
    public void longArray() {
    }
    @Test
    public void integerArray() {
    }

    @Test
    public void shortArray() {
    }

    @Test
    public void doubleArray() {
    }

    @Test
    public void floatArray() {
    }

    @Test
    public void booleanArray() {
    }

    @Test
    public void testStringListWithDefaultValue() {
    }

    @Test
    public void longList() {
    }

    @Test
    public void integerList() {
    }

    @Test
    public void shortList() {
    }

    @Test
    public void doubleList() {

    }

    @Test
    public void floatList() {
    }

    @Test
    public void booleanList() {
    }

    @Test
    public void testStringArray() {
    }

    @Test
    public void enumArray() {
    }

    @Test
    public void testLongArray() {
    }

    @Test
    public void testIntegerArray() {
    }

    @Test
    public void testShortArray() {
    }

    @Test
    public void testDoubleArray() {
    }

    @Test
    public void testFloatArray() {
    }

    @Test
    public void testBooleanArray() {
    }

    @Test
    public void testListOf() {
    }

    @Test
    public void testStringList() {
    }

    @Test
    public void testEnumList() {
    }

    @Test
    public void testLongList() {
    }

    @Test
    public void testIntegerList() {
    }

    @Test
    public void testShortList() {
    }

    @Test
    public void testDoubleList() {
    }

    @Test
    public void testFloatList() {
    }

    @Test
    public void testBooleanList() {
    }

    @Test
    public void testIsArrayAttribute() {
    }
    @Test
    public void testEnumArray() {
    }
}
