package liquer.alchemy.crypto.xml.c14n;

import liquer.alchemy.crypto.Identifier;
import org.w3c.dom.Node;

/**
 * http://www.w3.org/TR/xml-exc-c14n/
 */
public class ExclusiveCanonicalXml_1_0_OmitComments extends ExclusiveCanonicalXml_1_0_WithComments {

    @Override
    public String getIdentifier() { return Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS; }

    @Override
    public StringBuilder buildComment(Node node) {
        return new StringBuilder();
    }
}
