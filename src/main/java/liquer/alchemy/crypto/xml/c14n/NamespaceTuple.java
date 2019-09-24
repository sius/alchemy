package liquer.alchemy.crypto.xml.c14n;

final class NamespacesTuple {

    final StringBuilder builder;
    final String newDefaultNamespaceURI;

    NamespacesTuple(StringBuilder builder, String newDefaultNamespaceURI) {
        this.builder = builder;
        this.newDefaultNamespaceURI = newDefaultNamespaceURI;
    }
}