package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import org.w3c.dom.Node;

/**
 * https://www.w3.org/TR/2001/REC-xml-c14n-20010315
 */
public class CanonicalXml_1_0_OmitComments extends CanonicalXml_1_0_WithComments {

    @Override
    public String getIdentifier() { return Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS; }

    @Override
    public StringBuilder buildComment(Node node) {
        return new StringBuilder();
    }
}
