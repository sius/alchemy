package liquer.alchemy.crypto.xml;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextMapTest {

    @Test
    public void getPrefix_defaultNS() {
        NamespaceContext test = new NamespaceContextMap();
        String actual = test.getPrefix(XMLConstants.NULL_NS_URI);
        Assert.assertEquals(XMLConstants.DEFAULT_NS_PREFIX, actual);
    }

    @Test
    public void getPrefix_xml() {
        NamespaceContext test = new NamespaceContextMap();
        String actual = test.getPrefix(XMLConstants.XML_NS_URI);
        Assert.assertEquals(XMLConstants.XML_NS_PREFIX, actual);
    }

    @Test
    public void getPrefix_xmlns() {
        NamespaceContext test = new NamespaceContextMap();
        String actual = test.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        Assert.assertEquals(XMLConstants.XMLNS_ATTRIBUTE, actual);
    }
}
