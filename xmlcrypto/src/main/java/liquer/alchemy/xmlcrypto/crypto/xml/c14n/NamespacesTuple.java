package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

final class NamespacesTuple {

    public final StringBuilder builder;
    public final String newDefaultNamespaceURI;

    NamespacesTuple(StringBuilder builder, String newDefaultNamespaceURI) {
        this.builder = builder;
        this.newDefaultNamespaceURI = newDefaultNamespaceURI;
    }
}