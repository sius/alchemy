package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prevent signature wrapping attack
 */
public class SAXIdAttributeReader extends DefaultHandler {

    private final String idAttribute;
    private final String id;
    private List<String> elements;

    public List<String> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public SAXIdAttributeReader(String id, String idAttribute) {
        this.id = id;
        this.idAttribute = idAttribute;
        this.elements = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        final String idAttrValue = attributes.getValue(idAttribute);
        if (idAttrValue != null && idAttrValue.equals(id)) {
            elements.add(qName);
        }
    }
}
