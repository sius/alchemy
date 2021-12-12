package liquer.alchemy.xmlcrypto.support;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TypedMapTest {

    private static TypedMap TM_VALUES;
    private enum TestEnum { A, B, C };


    @BeforeAll
    static void init() {
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
    void testTryGetValue() {

        assertTrue(Boolean.TRUE == TM_VALUES.tryBooleanValue("Boolean").get());
        assertTrue(Short.MAX_VALUE == TM_VALUES.tryShortValue("Short").get());
        assertTrue(Integer.MAX_VALUE == TM_VALUES.tryIntegerValue("Integer").get());
        assertTrue(Float.MAX_VALUE == TM_VALUES.tryFloatValue("Float").get());
        assertTrue(Double.MAX_VALUE == TM_VALUES.tryDoubleValue("Double").get());
        assertTrue(Long.MAX_VALUE == TM_VALUES.tryLongValue("Long").get());
        assertTrue("Hello World".equals(TM_VALUES.tryStringValue("String").get()));
        assertTrue(TestEnum.A.equals(TM_VALUES.tryEnumValue(TestEnum.class, "Enum").get()));
    }

    @Test
    void testTryGetListValue() {
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

        assertArrayEquals(expectedBooleans.toArray(),
            tm.tryBooleanList("Boolean").get().toArray());
        assertArrayEquals(expectedShorts.toArray(),
            tm.tryShortList("Short").get().toArray());
        assertArrayEquals(expectedIntegers.toArray(),
            tm.tryIntegerList("Integer").get().toArray());
        assertArrayEquals(expectedFloats.toArray(),
            tm.tryFloatList("Float").get().toArray());
        assertArrayEquals(expectedDoubles.toArray(),
            tm.tryDoubleList("Double").get().toArray());
        assertArrayEquals(expectedLongs.toArray(),
            tm.tryLongList("Long").get().toArray());
        assertArrayEquals(expectedStrings.toArray(),
            tm.tryStringList("String").get().toArray());
    }

    @Test
    void testTryGetArrayValue() {
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

        assertArrayEquals(expectedBooleans,
            tm.tryBooleanArray("Boolean").get());
        assertArrayEquals(expectedShorts,
            tm.tryShortArray("Short").get());
        assertArrayEquals(expectedIntegers,
            tm.tryIntegerArray("Integer").get());
        assertArrayEquals(expectedFloats,
            tm.tryFloatArray("Float").get());
        assertArrayEquals(expectedDoubles,
            tm.tryDoubleArray("Double").get());
        assertArrayEquals(expectedLongs,
            tm.tryLongArray("Long").get());
        assertArrayEquals(expectedStrings,
            tm.tryStringArray("String").get());
    }

    @Test
    void testPrune() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        final TypedMap ptm = tm.prune("1", "2", "100");
        assertEquals("1", tm.get("1"));
        assertEquals(null, tm.get("100"));
        assertEquals(3, tm.size());
        assertEquals(2, ptm.size());
        assertEquals(null, ptm.get("0"));
        assertEquals("1", ptm.get("1"));
        assertEquals("2", ptm.get("2"));
        assertEquals(null, ptm.get("100"));
    }

    @Test
    void testSelfPrune() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        assertEquals("0", tm.get("0"));
        assertEquals("1", tm.get("1"));
        assertEquals("2", tm.get("2"));
        assertEquals(null, tm.get("100"));
        final TypedMap ptm = tm.selfPrune("1", "2", "100");

        assertEquals(2, ptm.size());
        assertEquals(null, ptm.get("0"));
        assertEquals("1", ptm.get("1"));
        assertEquals("2", ptm.get("2"));
        assertEquals(null, ptm.get("100"));

        assertTrue(tm == ptm);
    }

    @Test
    void testPutWithObjectKey() {
        final TypedMap tm = new TypedMap();
        Object key = "key";
        Object value = "value";
        tm.putWithObjectKey(key, value);
        assertTrue(tm.containsKey(key.toString()));
        assertTrue(tm.containsValue(value.toString()));
        assertEquals("value", tm.get(key.toString()));
    }

    @Test
    void testGet() {
        final TypedMap tm = new TypedMap();
        tm.put("0", "0");
        assertEquals("0", tm.get("0"));
        assertEquals(null, tm.get("1"));

    }

    @Test
    void valueOf() {
    }

    @Test
    void listOf() {
    }

    @Test
    void testDeleteString() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        String actual = tm.deleteString("0");
        assertEquals("0", actual);
        assertEquals(null, tm.get("0"));
        assertEquals("100", tm.deleteString("100", "100"));
        assertEquals(null, tm.deleteString("100"));
    }

    @Test
    void testDeleteLong() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        long actual = tm.deleteLong("0");
        assertEquals(0L, actual);
        assertEquals(null, tm.get("0"));
        assertEquals(100L, (long)tm.deleteLong("100", 100L));
        assertEquals(null, tm.deleteLong("100"));
    }

    @Test
    void testDeleteInteger() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        int actual = tm.deleteInteger("0");
        assertEquals(0, actual);
        assertEquals(null, tm.get("0"));
        assertEquals(100, (int)tm.deleteInteger("100", 100));
        assertEquals(null, tm.deleteInteger("100"));
    }

    @Test
    void testDeleteShort() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        short actual = tm.deleteShort("0");
        assertEquals((short)0, actual);
        assertEquals(null, tm.get("0"));
        assertEquals((short)100, (short)tm.deleteShort("100", (short)100));
        assertEquals(null, tm.deleteInteger("100"));
    }

    @Test
    void testDeleteDouble() {
        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        double actual = tm.deleteDouble("0");
        assertEquals(0.0, actual, 0);
        assertEquals(null, tm.get("0"));
        assertEquals(100.0, (double)tm.deleteDouble("100", 100.0), 0);
        assertEquals(null, tm.deleteDouble("100"));
    }

    @Test
    void testDeleteFloat() {

        final TypedMap tm = new TypedMap(YashMap.of("0", "0", "1", "1", "2", "2"));
        float actual = tm.deleteFloat("0");
        assertEquals(0.0f, actual, 0);
        assertEquals(null, tm.get("0"));
        assertEquals(100.0f, (double)tm.deleteFloat("100", 100.0f), 0);
        assertEquals(null, tm.deleteFloat("100"));
    }

    @Test
    void testDeleteBoolean() {

        final TypedMap tm = new TypedMap(YashMap.of("0", true, "1", "1", "2", "2"));
        boolean actual = tm.deleteBoolean("0");
        assertEquals(true, actual);
        assertEquals(null, tm.get("0"));
        assertEquals(false, tm.deleteBoolean("100", false));
        assertEquals(null, tm.deleteBoolean("100"));
    }

    @Test
    void testDeleteStrings() {
        final TypedMap tm = new TypedMap(YashMap.of("key", "a", "key", "b", "key", "c"));
        assertEquals(1, tm.size());
        final String[] actual = tm.deleteStrings("key");
        assertArrayEquals(new String[] {"a", "b", "c"}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteLongs() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1L, "key", 2L, "key", 3L));
        assertEquals(1, tm.size());
        final Long[] actual = tm.deleteLongs("key");
        assertArrayEquals(new Long[] {1L, 2L, 3L}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteIntegers() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1, "key", 2, "key", 3));
        assertEquals(1, tm.size());
        final Integer[] actual = tm.deleteIntegers("key");
        assertArrayEquals(new Integer[] {1, 2, 3}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteShorts() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1, "key", 2, "key", 3));
        assertEquals(1, tm.size());
        final Short[] actual = tm.deleteShorts("key");
        assertArrayEquals(new Short[] {1, 2, 3}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteDoubles() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1.0, "key", 2.0, "key", 3.0));
        assertEquals(1, tm.size());
        final Double[] actual = tm.deleteDoubles("key");
        assertArrayEquals(new Double[] {1.0, 2.0, 3.0}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteFloats() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1f, "key", 2f, "key", 3f));
        assertEquals(1, tm.size());
        final Float[] actual = tm.deleteFloats("key");
        assertArrayEquals(new Float[] {1f, 2f, 3f}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteBooleans() {
        final TypedMap tm = new TypedMap(YashMap.of("key", true, "key", false, "key", true));
        assertEquals(1, tm.size());
        final Boolean[] actual = tm.deleteBooleans("key");
        assertArrayEquals(new Boolean[] {true, false, true}, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteLongWithDefaultValue() {
        assertEquals(1L, (long)(new TypedMap()).deleteLong("key", 1L));
    }

    @Test
    void testDeleteIntegerWithDefaultValue() {
        assertEquals(1, (int)(new TypedMap()).deleteInteger("key", 1));
    }

    @Test
    void testDeleteShortWithDefaultValue() {
        assertEquals((short)1, (short)(new TypedMap()).deleteShort("key", (short)1));
    }

    @Test
    void testDeleteDoubleWithDefaultValue() {
        assertEquals(1.0, (double)(new TypedMap()).deleteDouble("key", 1.0), 0.0);
    }

    @Test
    void testDeleteFloatWithDefaultValue() {
        assertEquals(1.0f, (float)(new TypedMap()).deleteFloat("key", 1.0f), 0.0);
    }

    @Test
    void testDeleteBooleanWithDefaultValue() {
        assertEquals(true, (new TypedMap()).deleteBoolean("key", true));
    }

    @Test
    void testDeleteStringsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final String[] expected = new String[] {"a", "b", "c"};
        final String[] actual = tm.deleteStrings("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteLongsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Long[] expected = new Long[] {1L, 2L, 3L};
        final Long[] actual = tm.deleteLongs("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteIntegersWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Integer[] expected = new Integer[] {1, 2, 3};
        final Integer[] actual = tm.deleteIntegers("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteShortsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Short[] expected = new Short[] {1, 2, 3};
        final Short[] actual = tm.deleteShorts("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteDoublesWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Double[] expected = new Double[] {1.0, 2.0, 3.0};
        final Double[] actual = tm.deleteDoubles("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteFloatsWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Float[] expected = new Float[] {1f, 2f, 3f};
        final Float[] actual = tm.deleteFloats("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testDeleteBooleansWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        assertEquals(0, tm.size());
        final Boolean[] expected = new Boolean[] {true, false, true};
        final Boolean[] actual = tm.deleteBooleans("key", expected);
        assertArrayEquals(expected, actual);
        assertEquals(0, tm.size());
    }

    @Test
    void testStringValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", 1));
        final String actual = tm.stringValue("key");
        assertEquals("1", actual);
    }

    @Test
    void tryStringValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", "test"));
        String actual = tm.tryStringValue("key").get();
        assertEquals("test", actual);
        try {
            tm.tryStringValue("key1").get();
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }

        try {
            tm.tryStringValue("key1")
                .orElseThrow(() -> new Exception("test"));
        } catch (Exception e) {
            assertEquals("test", e.getMessage());
        }

        actual = tm.tryStringValue("key1").orElse("hello");
        assertEquals("hello", actual);
        actual = tm.tryStringValue("key1").orElseGet(()->null);
        assertEquals(null, actual);


    }


    @Test
    void testEnumValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", TestEnum.A, "key2", 1));
        final TestEnum actual = tm.enumValue(TestEnum.class, "key");
        assertEquals(TestEnum.A, actual);
    }

    @Test
    void testLongValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Long.MAX_VALUE));
        final long actual = tm.longValue("key");
        assertEquals(Long.MAX_VALUE, actual);
    }

    @Test
    void testIntegerValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Integer.MAX_VALUE));
        final int actual = tm.integerValue("key");
        assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    void testShortValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Short.MAX_VALUE));
        final short actual = tm.shortValue("key");
        assertEquals(Short.MAX_VALUE, actual);
    }

    @Test
    void testDoubleValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Double.MAX_VALUE));
        final double actual = tm.doubleValue("key");
        assertEquals(Double.MAX_VALUE, actual, 0d);
    }

    @Test
    void testFloatValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Float.MAX_VALUE));
        final float actual = tm.floatValue("key");
        assertEquals(Float.MAX_VALUE, actual, 0f);
    }

    @Test
    void testBooleanValue() {
        final TypedMap tm = new TypedMap(YashMap.of("key", Boolean.TRUE));
        final boolean actual = tm.booleanValue("key");
        assertEquals(Boolean.TRUE, actual);
    }

    @Test
    void availableKeys() {
        final TypedMap tm = new TypedMap(YashMap.of("a", 1, "b", 2, "c", 3));
        String actual = tm.availableKeys("0", "1", "a", "f", "b", "d", "e").collect(Collectors.joining(","));
        assertEquals("a,b", actual);
    }

    @Test
    void testStringValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final String actual = tm.stringValue("key", "test");
        assertEquals("test", actual);
    }

    @Test
    void testEnumValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final TestEnum actual = tm.enumValue(TestEnum.class, "key", TestEnum.A);
        assertEquals(TestEnum.A, actual);
    }

    @Test
    void testLongValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final long actual = tm.longValue("key", Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, actual);
    }

    @Test
    void testIntegerValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final int actual = tm.integerValue("key", Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, actual);
    }

    @Test
    void testShortValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final short actual = tm.shortValue("key", Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, actual);
    }

    @Test
    void testDoubleValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final double actual = tm.doubleValue("key", Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, actual, 0d);
    }

    @Test
    void testFloatValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final float actual = tm.floatValue("key", Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, actual,0f);
    }

    @Test
    void testBooleanValueWithDefaultValue() {
        final TypedMap tm = new TypedMap();
        final boolean actual = tm.booleanValue("key", Boolean.TRUE);
        assertEquals(Boolean.TRUE, actual);
    }

    @Test
    void stringArray() {
    }

    @Test
    void longArray() {
    }
    @Test
    void integerArray() {
    }

    @Test
    void shortArray() {
    }

    @Test
    void doubleArray() {
    }

    @Test
    void floatArray() {
    }

    @Test
    void booleanArray() {
    }

    @Test
    void testStringListWithDefaultValue() {
    }

    @Test
    void longList() {
    }

    @Test
    void integerList() {
    }

    @Test
    void shortList() {
    }

    @Test
    void doubleList() {

    }

    @Test
    void floatList() {
    }

    @Test
    void booleanList() {
    }

    @Test
    void testStringArray() {
    }

    @Test
    void enumArray() {
    }

    @Test
    void testLongArray() {
    }

    @Test
    void testIntegerArray() {
    }

    @Test
    void testShortArray() {
    }

    @Test
    void testDoubleArray() {
    }

    @Test
    void testFloatArray() {
    }

    @Test
    void testBooleanArray() {
    }

    @Test
    void testListOf() {
    }

    @Test
    void testStringList() {
    }

    @Test
    void testEnumList() {
    }

    @Test
    void testLongList() {
    }

    @Test
    void testIntegerList() {
    }

    @Test
    void testShortList() {
    }

    @Test
    void testDoubleList() {
    }

    @Test
    void testFloatList() {
    }

    @Test
    void testBooleanList() {
    }

    @Test
    void testIsArrayAttribute() {
    }
    @Test
    void testEnumArray() {
    }
}
