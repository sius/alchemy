package liquer.alchemy.xmlcrypto.support;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Map;

public class YashMapTest {

    @Test
    public void testOf() {
        final YashMap y = YashMap.of("0", "1", "0", "2");
        Assert.assertEquals(1, y.size());

        Object arr = y.get("0");
        Assert.assertTrue(arr.getClass().isArray());
        Assert.assertEquals(2, Array.getLength(arr));
        Assert.assertEquals("1", Array.get(arr, 0));
        Assert.assertEquals("2", Array.get(arr, 1));
        Assert.assertArrayEquals(new Object[] {"1", "2"}, (Object[])arr);

    }

    @Test
    public void testDup() {
        final YashMap org = YashMap.of("0", "1");
        final YashMap dup = org.dup();
        Assert.assertEquals(org.get("0"), dup.get("0"));
        Assert.assertEquals(org.size(), dup.size());
        Assert.assertFalse(org == dup);
    }

    @Test
    public void testRemove() {
        final YashMap org = YashMap.of("0", "1");
        Object removed = org.remove("0");
        Assert.assertEquals("1", removed);
        Assert.assertTrue(org.isEmpty());
    }

    @Test
    public void testClear() {
        final YashMap org = YashMap.of("0", "1");
        Assert.assertFalse(org.isEmpty());
        org.clear();
        Assert.assertTrue(org.isEmpty());
    }

    @Test
    public void testPutAll() {
        final YashMap org = YashMap.of();
        org.putAll(YashMap.of("0", 0,"1", 1));
        Assert.assertEquals(2, org.size());
        Assert.assertEquals(0, org.get("0"));
        Assert.assertEquals(1, org.get("1"));
    }

    @Test
    public void testPutIfAbsent() {
        final YashMap org = YashMap.of("0", "1");
        Assert.assertEquals("1", org.get("0"));
        org.putIfAbsent("0", "2");
        Assert.assertEquals("1", org.get("0"));
        org.putIfAbsent("1", "2");
        Assert.assertEquals("2", org.get("1"));

    }

    @Test
    public void testReplace() {
        final YashMap org = YashMap.of("0", "1");
        boolean actual = org.replace("0", "1", "2");
        Assert.assertEquals(true, actual);
        actual = org.replace("0", "1", "2");
        Assert.assertEquals(false, actual);
        Assert.assertEquals("2", org.get("0"));
    }

    @Test
    public void testReplaceAll() {
    }

    @Test
    public void testPut() {
        YashMap y = new YashMap();
        Assert.assertEquals(0, y.size());
        y.put("0", 0);
        Assert.assertEquals(1, y.size());
        Assert.assertEquals(0, y.get("0"));
    }

    @Test
    public void testInvert() {
        final YashMap org = YashMap.of(0, 1, "a", "b");
        final Map<Object, String> inv = org.invert();
        Assert.assertEquals(2, inv.size());
        Assert.assertEquals("0", inv.get(1));
        Assert.assertEquals("a", inv.get("b"));

    }

    @Test
    public void testMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        final YashMap m = y1.merge(y2);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals("b", m.get("0"));
        Assert.assertEquals("1", m.get("a"));
        Assert.assertEquals("y", m.get("x"));
        Assert.assertTrue(m != y1);
        Assert.assertTrue(m != y2);
    }

    @Test
    public void testSelfMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        y1.selfMerge(y2);
        Assert.assertEquals(3, y1.size());
        Assert.assertEquals("b", y1.get("0"));
        Assert.assertEquals("1", y1.get("a"));
        Assert.assertEquals("y", y1.get("x"));
        Assert.assertTrue(y1 != y2);
    }

    @Test
    public void testReverseMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        final YashMap m = y1.reverseMerge(y2);
        Assert.assertEquals(3, m.size());
        Assert.assertEquals("1", m.get("0"));
        Assert.assertEquals("b", m.get("a"));
        Assert.assertEquals("y", m.get("x"));
        Assert.assertTrue(m != y1);
        Assert.assertTrue(m != y2);
    }

    @Test
    public void testSelfReverseMerge() {
        final YashMap y1 = YashMap.of("0", "1", "a", "b");
        final YashMap y2 = YashMap.of("0", "b", "a", "1", "x", "y");
        y1.selfReverseMerge(y2);
        Assert.assertEquals(3, y1.size());
        Assert.assertEquals("1", y1.get("0"));
        Assert.assertEquals("b", y1.get("a"));
        Assert.assertEquals("y", y1.get("x"));
        Assert.assertTrue(y1 != y2);
    }

    @Test
    public void testDelete() {
        final YashMap y = YashMap.of("0", "b", "a", "1", "x", "y");
        Assert.assertEquals(3, y.size());
        Object actual = y.delete("a");
        Assert.assertEquals(2, y.size());
        Assert.assertEquals("1", actual);

    }

    @Test
    public void testStringifyValues() {
        final YashMap y1 = YashMap.of("a", 1, "b", 2);
        final YashMap y2 = y1.stringifyValues();
        Assert.assertEquals(2, y2.size());
        Assert.assertEquals("1", y2.get("a"));
        Assert.assertEquals("2", y2.get("b"));
    }

    @Test
    public void testSelfStringifyValues() {
        final YashMap y1 = YashMap.of("a", 1, "b", 2);
        y1.selfStringifyValues();
        Assert.assertEquals(2, y1.size());
        Assert.assertEquals("1", y1.get("a"));
        Assert.assertEquals("2", y1.get("b"));
    }
}
