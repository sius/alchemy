package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.Identifier;
import org.w3c.dom.Node;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class X509Data extends SafeNodeReader {

    private final String issuerName;
    private final String serialNumber;
    private final String key;

    public X509Data(Node node) {

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

    public X509Certificate getCertificate() throws CertificateException {
       return CryptoLimericks.readX509Certificate(key.getBytes());
    }
}

