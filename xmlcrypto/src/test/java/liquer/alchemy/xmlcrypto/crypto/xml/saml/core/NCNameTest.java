package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import org.junit.Assert;
import org.junit.Test;

public class NCNameTest {

    @Test
    public void testValidDefaultRandomNCName() {
        NCName actual = new NCName();
        Assert.assertTrue(NCName.isValidNCName(actual.toString()));
    }

    @Test
    public void testValidNCNameString() {
        Assert.assertTrue(NCName.isValidNCName("a123"));
        Assert.assertTrue(NCName.isValidNCName("_123"));
    }

    @Test
    public void testInvalidNCNameString() {
        Assert.assertFalse(NCName.isValidNCName("123"));
    }
}
