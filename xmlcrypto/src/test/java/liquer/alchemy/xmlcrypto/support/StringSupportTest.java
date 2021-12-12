package liquer.alchemy.xmlcrypto.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringSupportTest {

    @Test
    void testIsNullOrEmpty() {
        assertTrue(StringSupport.isNullOrEmpty(null));
        assertTrue(StringSupport.isNullOrEmpty(""));
        assertFalse(StringSupport.isNullOrEmpty("  "));
        assertFalse(StringSupport.isNullOrEmpty("a"));

        final Object o = new Object();
        assertFalse(StringSupport.isNullOrEmpty(o));
    }

    @Test
    void testIsNullEmptyOrBlank() {
        assertTrue(StringSupport.isNullEmptyOrBlank(null));
        assertTrue(StringSupport.isNullEmptyOrBlank(""));
        assertTrue(StringSupport.isNullEmptyOrBlank("  "));
        assertFalse(StringSupport.isNullEmptyOrBlank("a"));

        final Object o = new Object();
        assertFalse(StringSupport.isNullEmptyOrBlank(o));
    }

    @Test
    void testNotNullOrEmpty() {
        assertFalse(StringSupport.notNullOrEmpty(null));
        assertFalse(StringSupport.notNullOrEmpty(""));
        assertTrue(StringSupport.notNullOrEmpty("  "));
        assertTrue(StringSupport.notNullOrEmpty("a"));

        final Object o = new Object();
        assertTrue(StringSupport.notNullOrEmpty(o));
    }

    @Test
    void testNotNullEmptyOrBlank() {
        assertFalse(StringSupport.notNullEmptyOrBlank(null));
        assertFalse(StringSupport.notNullEmptyOrBlank(""));
        assertFalse(StringSupport.notNullEmptyOrBlank("  "));
        assertTrue(StringSupport.notNullEmptyOrBlank("a"));

        final Object o = new Object();
        assertTrue(StringSupport.notNullEmptyOrBlank(o));
    }

    @Test
    void stringify() {
        String actual = StringSupport.stringify(123);
        assertEquals("123", actual);
        assertEquals("", StringSupport.stringify(null));
    }
}
