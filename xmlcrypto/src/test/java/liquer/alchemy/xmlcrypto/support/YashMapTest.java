package liquer.alchemy.xmlcrypto.support;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YashMapTest {

    @Test
    void testOf() {
        final YashMap y = YashMap.of("0", "1", "0", "2");
        assertEquals(1, y.size());

        Object arr = y.get("0");
        assertTrue(arr.getClass().isArray());
        assertEquals(2, Array.getLength(arr));
        assertEquals("1", Array.get(arr, 0));
        assertEquals("2", Array.get(arr, 1));
        assertArrayEquals(new Object[] {"1", "2"}, (Object[])arr);

    }

    @Test
    void testDup() {
        final YashMap org = YashMap.of("0", "1");
        final YashMap dup = org.dup();
        assertEquals(org.get("0"), dup.get("0"));
        assertEquals(org.size(), dup.size());
        assertFalse(org == dup);
    }

    @Test
    void testRemove() {
        final YashMap org = YashMap.of("0", "1");
        Object removed = org.remove("0");
        assertEquals("1", removed);
        assertTrue(org.isEmpty());
    }

    @Test
    void testClear() {
        final YashMap org = YashMap.of("0", "1");
        assertFalse(org.isEmpty());
        org.clear();
        assertTrue(org.isEmpty());
    }

    @Test
    void testPutAll() {
        final YashMap org = YashMap.of();
        org.putAll(YashMap.of("0", 0,"1", 1));
        assertEquals(2, org.size());
        assertEquals(0, org.get("0"));
        assertEquals(1, org.get("1"));
    }

    @Test
    void testPutIfAbsent() {
        final YashMap org = YashMap.of("0", "1");
        assertEquals("1", org.get("0"));
        org.putIfAbsent("0", "2");
        assertEquals("1", org.get("0"));
        org.putIfAbsent("1", "2");
        assertEquals("2", org.get("1"));

    }

    @Test
    void testReplace() {
        final YashMap org = YashMap.of("0", "1");
        boolean actual = org.replace("0", "1", "2");
        assertEquals(true, actual);
        actual = org.replace("0", "1", "2");
        assertEquals(false, actual);
        assertEquals("2", org.get("0"));
    }

    @Test
    void testReplaceAll() {
    }

    @Test
    void testPut() {
        YashMap y = new YashMap();
        assertEquals(0, y.size());
        y.put("0", 0);
        assertEquals(1, y.size());
        assertEquals(0, y.get("0"));
    }

    @Test
    void testInvert() {
        final YashMap org = YashMap.of(0, 1, "a", "b");
        final Map<Object, String> inv = org.invert();
        assertEquals(2, inv.size());
        assertEquals("0", inv.get(1));
        assertEquals("a", inv.get("b"));

    }

    @Test
    void testMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        final YashMap m = y1.merge(y2);
        assertEquals(3, m.size());
        assertEquals("b", m.get("0"));
        assertEquals("1", m.get("a"));
        assertEquals("y", m.get("x"));
        assertTrue(m != y1);
        assertTrue(m != y2);
    }

    @Test
    void testSelfMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        y1.selfMerge(y2);
        assertEquals(3, y1.size());
        assertEquals("b", y1.get("0"));
        assertEquals("1", y1.get("a"));
        assertEquals("y", y1.get("x"));
        assertTrue(y1 != y2);
    }

    @Test
    void testReverseMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        final YashMap m = y1.reverseMerge(y2);
        assertEquals(3, m.size());
        assertEquals("1", m.get("0"));
        assertEquals("b", m.get("a"));
        assertEquals("y", m.get("x"));
        assertTrue(m != y1);
        assertTrue(m != y2);
    }

    @Test
    void testSelfReverseMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        y1.selfReverseMerge(y2);
        assertEquals(3, y1.size());
        assertEquals("1", y1.get("0"));
        assertEquals("b", y1.get("a"));
        assertEquals("y", y1.get("x"));
        assertTrue(y1 != y2);
    }

    @Test
    void testDelete() {
        final YashMap y = YashMap.of("0", "b", "a", "1", "x", "y");
        assertEquals(3, y.size());
        Object actual = y.delete("a");
        assertEquals(2, y.size());
        assertEquals("1", actual);

    }

    @Test
    void testStringifyValues() {
        final YashMap y1 = YashMap.of("a", 1, "b", 2);
        final YashMap y2 = y1.stringifyValues();
        assertEquals(2, y2.size());
        assertEquals("1", y2.get("a"));
        assertEquals("2", y2.get("b"));
    }

    @Test
    void testSelfStringifyValues() {
        final YashMap y1 = YashMap.of("a", 1, "b", 2);
        y1.selfStringifyValues();
        assertEquals(2, y1.size());
        assertEquals("1", y1.get("a"));
        assertEquals("2", y1.get("b"));
    }
}
