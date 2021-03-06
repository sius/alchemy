package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.xml.core.StaticNamespaceContext;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class StaticNamespaceContextTest {

    @Test
    public void testGetPrefix_defaultNS() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.NULL_NS_URI);
        Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, actual);
    }

    @Test
    public void testGetPrefix_xml() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.XML_NS_URI);
        Assert.assertEquals(XMLConstants.XML_NS_PREFIX, actual);
    }

    @Test
    public void testGetPrefix_xmlns() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        Assert.assertEquals(XMLConstants.XMLNS_ATTRIBUTE, actual);
    }
}
