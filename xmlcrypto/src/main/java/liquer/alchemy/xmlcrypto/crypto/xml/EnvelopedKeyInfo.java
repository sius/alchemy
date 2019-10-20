package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.core.NodeReader;
import liquer.alchemy.xmlcrypto.crypto.xml.core.X509DataImpl;
import org.w3c.dom.Node;

import java.util.List;

public class EnvelopedKeyInfo extends NodeReader implements KeyInfo {

    private X509Data x509Data;

    @Override
    public String getKey(List<Node> keyInfo) {
        if (keyInfo == null || keyInfo.isEmpty()) {
            return null;
        }
        Node node = keyInfo.get(0);
        final String issuerName = readTextContent(
            readFirst(readElementsByTagNameNS(
                node,
                Identifier.DEFAULT_NS_URI,
                "X509IssuerName")));

        final String serialNumber = readTextContent(
            readFirst(readElementsByTagNameNS(
                node,
                Identifier.DEFAULT_NS_URI,
                "X509SerialNumber")));

        final String key = readTextContent(
            readFirst(readElementsByTagNameNS(
                node,
                Identifier.DEFAULT_NS_URI,
                "X509Certificate")));

        x509Data = new X509DataImpl(
            issuerName,
            serialNumber,
            key);

        return x509Data.getKey();
    }

    @Override
    public X509Data getX509Data() { return x509Data; }
}