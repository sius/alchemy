package liquer.alchemy.xmlcrypto.crypto.xml.core;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the NamespaceContext
 * to evaluate XPath expressions with full qualified names
 */
public class NamespaceContextMap extends HashMap<String, String> implements NamespaceContext {


    public  NamespaceContextMap() {
        initWithDefaultEntries();
    }

    @Override
    public String put(String prefix, String namespaceURI) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI cannot be null");
        }
        switch (prefix) {
            case XMLConstants.DEFAULT_NS_PREFIX: return super.put(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
            case XMLConstants.XML_NS_PREFIX: return XMLConstants.XML_NS_URI;
            case XMLConstants.XMLNS_ATTRIBUTE: return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            default: return super.put(prefix, namespaceURI);
        }
    }

    @Override
    public String remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        map.entrySet().forEach(i -> this.put(i.getKey(), i.getValue()));
    }

    @Override
    public void clear() {
        super.clear();
        initWithDefaultEntries();
    }

    private void initWithDefaultEntries() {
        super.put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
        super.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        super.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        return this.getOrDefault(prefix, null);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        final Iterator prefixes = getPrefixes(namespaceURI);

        if (prefixes.hasNext()) {
            return prefixes.next().toString();
        }
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI cannot be null");
        }

        // unbound Namespace URI
        if (!this.values().contains(namespaceURI)) {
            return Collections.emptyIterator();
        }

        // bound Namespace URI
        switch (namespaceURI) {
            case XMLConstants.XML_NS_URI:
                return Collections.unmodifiableList(
                        Arrays.asList(XMLConstants.XML_NS_PREFIX)).iterator();
            case XMLConstants.XMLNS_ATTRIBUTE_NS_URI:
                return Collections.unmodifiableList(
                        Arrays.asList(XMLConstants.XMLNS_ATTRIBUTE)).iterator();
            default:
                return Collections.unmodifiableList(super.entrySet().stream()
                        .filter(e -> e.getValue().equals(namespaceURI))
                        .map(e -> e.getKey())
                        .collect(Collectors.toList())).iterator();
        }
    }
}
