package liquer.alchemy.xmlcrypto.crypto.xml.core;

/**
 * A Tuple containing the prefix and its namespaceURI
 */
public final class PrefixNamespaceTuple {

    public final String prefix;
    public final String namespaceURI;

    public PrefixNamespaceTuple(String prefix, String namespaceURI) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }
}
