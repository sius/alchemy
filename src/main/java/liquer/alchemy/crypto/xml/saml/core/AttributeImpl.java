package liquer.alchemy.crypto.xml.saml.core;

import liquer.alchemy.crypto.xml.saml.Attribute;
import liquer.alchemy.crypto.xml.core.NodeReader;
import org.w3c.dom.Node;

class AttributeImpl extends NodeReader implements Attribute {

    private final String name;
    private final String value;

    AttributeImpl(Node node) {
        name = readNamedItem(node, "Name");
        value = readTextContent(node);
    }

    public String getName() { return name; }

    public String getValue() {
        return value;
    }
}
