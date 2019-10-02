package liquer.alchemy.crypto.xml.c14n;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.XPathSupport;
import org.w3c.dom.Node;

/**
 *
 */
public class EnvelopedSignature implements CanonicalXml {

    private final static String XPATH_EXPR =
            ".//*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']";

    @Override
    public Node apply(Node node, CanonicalOptions options) {
        return XPathSupport.deleteChild(node, XPATH_EXPR);
    }

    @Override
    public String getIdentifier() { return Identifier.ENVELOPED_SIGNATURE; }


}


