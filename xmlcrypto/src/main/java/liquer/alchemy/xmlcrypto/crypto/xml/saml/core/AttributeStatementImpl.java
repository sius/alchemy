package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.saml.Attribute;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AttributeStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AttributeStatementImpl implements AttributeStatement {

    private final List<Attribute> attributes;

    AttributeStatementImpl() {
        attributes = new ArrayList<>();
    }

    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
}
