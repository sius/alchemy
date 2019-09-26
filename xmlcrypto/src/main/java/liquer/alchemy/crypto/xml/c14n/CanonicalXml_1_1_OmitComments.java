package liquer.alchemy.crypto.xml.c14n;

import liquer.alchemy.crypto.Identifier;
import org.w3c.dom.Node;

public class CanonicalXml_1_1_OmitComments extends CanonicalXml_1_1_WithComments{

    @Override
    public String getIdentifier() { return Identifier.CANONICAL_XML_1_1_OMIT_COMMENTS; }

    @Override
    public StringBuilder buildComment(Node node) {
        return new StringBuilder();
    }
}
