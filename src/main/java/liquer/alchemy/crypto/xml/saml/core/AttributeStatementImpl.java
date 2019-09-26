package liquer.alchemy.crypto.xml.saml.core;

import liquer.alchemy.crypto.xml.saml.Attribute;
import liquer.alchemy.crypto.xml.saml.AttributeStatement;
import org.w3c.dom.Node;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AttributeStatementImpl implements AttributeStatement {

    private final List<Attribute> attributes;

    AttributeStatementImpl(Node node, BiFunction<Node, String, Stream<Node>> select) {

        attributes = select.apply(node, "./saml:Attribute")
                .map(AttributeImpl::new)
                .collect(Collectors.toList());
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
