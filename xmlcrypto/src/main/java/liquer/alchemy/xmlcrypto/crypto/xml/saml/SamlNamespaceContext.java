package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.core.StaticNamespaceContext;

import javax.xml.XMLConstants;

public final class SamlNamespaceContext extends StaticNamespaceContext {

    private static final long serialVersionUID = 0L;

    private SamlNamespaceContext() {
        super();
    }

    public static SamlNamespaceContext newInstance() {
        SamlNamespaceContext ret = new SamlNamespaceContext();
        ret.put(XMLConstants.DEFAULT_NS_PREFIX, Identifier.DEFAULT_NS_URI);
        ret.put(Identifier.SAML2_NS_PREFIX, Identifier.SAML2_NS_URI);
        ret.put(Identifier.WSSE_NS_PREFIX, Identifier.WSSE_NS_SCHEMA_LOCATION);
        ret.put(Identifier.SOAP_NS_PREFIX, Identifier.SOAP_SCHEMA_LOCATION);
        return ret;
    }
}
