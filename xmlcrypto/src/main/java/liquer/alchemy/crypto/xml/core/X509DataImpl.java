package liquer.alchemy.crypto.xml.core;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.X509Data;
import org.w3c.dom.Node;

public class X509DataImpl extends NodeReader implements X509Data {

    private final String issuerName;
    private final String serialNumber;
    private final String key;

    public X509DataImpl(Node node) {

        issuerName = readTextContent(
                readElementsStreamByTagNameNS(
                        node,
                        Identifier.DEFAULT_NS_URI,
                        "X509IssuerName").findFirst());

        serialNumber = readTextContent(
                readElementsStreamByTagNameNS(
                        node,
                        Identifier.DEFAULT_NS_URI,
                        "X509SerialNumber").findFirst());

        key = readTextContent(
                readElementsStreamByTagNameNS(
                        node,
                        Identifier.DEFAULT_NS_URI,
                        "X509Certificate").findFirst());
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getKey() {
        return key;
    }
}

