package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NCNameTest {

    @Test
    public void testValidDefaultRandomNCName() {
        NCName actual = new NCName();
        assertTrue(NCName.isValidNCName(actual.toString()));
    }

    @Test
    public void testValidNCNameString() {
        assertTrue(NCName.isValidNCName("a123"));
        assertTrue(NCName.isValidNCName("_123"));
    }

    @Test
    public void testInvalidNCNameString() {
        assertFalse(NCName.isValidNCName("123"));
    }
}
