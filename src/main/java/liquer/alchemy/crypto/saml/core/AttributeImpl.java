package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.Attribute;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import org.w3c.dom.Node;

class AttributeImpl extends SafeNodeReader implements Attribute {

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
