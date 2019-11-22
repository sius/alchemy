package liquer.alchemy.xmlcrypto.support;

import org.junit.Assert;
import org.junit.Test;

public class StringSupportTest {

    @Test
    public void testIsNullOrEmpty() {
        Assert.assertTrue(StringSupport.isNullOrEmpty(null));
        Assert.assertTrue(StringSupport.isNullOrEmpty(""));
        Assert.assertFalse(StringSupport.isNullOrEmpty("  "));
        Assert.assertFalse(StringSupport.isNullOrEmpty("a"));

        final Object o = new Object();
        Assert.assertFalse(StringSupport.isNullOrEmpty(o));
    }

    @Test
    public void testIsNullEmptyOrBlank() {
        Assert.assertTrue(StringSupport.isNullEmptyOrBlank(null));
        Assert.assertTrue(StringSupport.isNullEmptyOrBlank(""));
        Assert.assertTrue(StringSupport.isNullEmptyOrBlank("  "));
        Assert.assertFalse(StringSupport.isNullEmptyOrBlank("a"));

        final Object o = new Object();
        Assert.assertFalse(StringSupport.isNullEmptyOrBlank(o));
    }

    @Test
    public void testNotNullOrEmpty() {
        Assert.assertFalse(StringSupport.notNullOrEmpty(null));
        Assert.assertFalse(StringSupport.notNullOrEmpty(""));
        Assert.assertTrue(StringSupport.notNullOrEmpty("  "));
        Assert.assertTrue(StringSupport.notNullOrEmpty("a"));

        final Object o = new Object();
        Assert.assertTrue(StringSupport.notNullOrEmpty(o));
    }

    @Test
    public void testNotNullEmptyOrBlank() {
        Assert.assertFalse(StringSupport.notNullEmptyOrBlank(null));
        Assert.assertFalse(StringSupport.notNullEmptyOrBlank(""));
        Assert.assertFalse(StringSupport.notNullEmptyOrBlank("  "));
        Assert.assertTrue(StringSupport.notNullEmptyOrBlank("a"));

        final Object o = new Object();
        Assert.assertTrue(StringSupport.notNullEmptyOrBlank(o));
    }

    @Test
    public void stringify() {
        String actual = StringSupport.stringify(123);
        Assert.assertEquals("123", actual);
        Assert.assertEquals("", StringSupport.stringify(null));
    }
}
