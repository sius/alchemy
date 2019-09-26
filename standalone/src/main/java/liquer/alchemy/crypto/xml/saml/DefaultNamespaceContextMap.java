package liquer.alchemy.crypto.xml.saml;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.core.NamespaceContextMap;

import javax.xml.XMLConstants;

public class DefaultNamespaceContextMap extends NamespaceContextMap {

    public DefaultNamespaceContextMap() {
        super();
        put(XMLConstants.DEFAULT_NS_PREFIX, Identifier.DEFAULT_NS_URI);
        put(Identifier.SAML2_NS_PREFIX, Identifier.SAML2_NS_URI);
        put(Identifier.WSSE_NS_PREFIX, Identifier.WSSE_NS_URI);
        put(Identifier.SOAP_NS_PREFIX, Identifier.SOAP_NS_URI);
    }
}
