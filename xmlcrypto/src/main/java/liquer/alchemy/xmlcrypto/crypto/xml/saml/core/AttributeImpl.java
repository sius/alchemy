package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.saml.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AttributeImpl implements Attribute {

    private final String name;
    private final List<String> values;

    AttributeImpl(String name) {
        this.name = name;
        this.values = new ArrayList();
    }

    public String getName() { return name; }

    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }
    void addValue(String value) {
        values.add(value);
    }
}
