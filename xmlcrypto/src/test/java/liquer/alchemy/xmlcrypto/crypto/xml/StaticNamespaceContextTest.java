package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.xml.core.StaticNamespaceContext;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticNamespaceContextTest {

    @Test
    void testGetPrefix_defaultNS() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.NULL_NS_URI);
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, actual);
    }

    @Test
    void testGetPrefix_xml() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.XML_NS_URI);
        assertEquals(XMLConstants.XML_NS_PREFIX, actual);
    }

    @Test
    void testGetPrefix_xmlns() {
        NamespaceContext test = new StaticNamespaceContext();
        String actual = test.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        assertEquals(XMLConstants.XMLNS_ATTRIBUTE, actual);
    }
}
