package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import org.w3c.dom.Node;

/**
 * https://www.w3.org/TR/xml-c14n/
 */
public class CanonicalXml_1_1_WithComments implements CanonicalXml {

    @Override
    public Node apply(Node node, CanonicalOptions options) throws CanonicalizationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getIdentifier() { return Identifier.CANONICAL_XML_1_1_WITH_COMMENTS; }
}
