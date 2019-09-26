package liquer.alchemy.crypto.xml.saml;

import liquer.alchemy.crypto.xml.KeyInfo;
import liquer.alchemy.crypto.xml.core.NodeReader;
import liquer.alchemy.crypto.xml.X509Data;
import liquer.alchemy.crypto.xml.core.X509DataImpl;
import org.w3c.dom.Node;

import java.util.List;

public class EnvelopedKeyInfo extends NodeReader implements KeyInfo {

    private X509Data x509Data;

    @Override
    public String getKey(List<Node> keyInfo) {
        if (keyInfo == null || keyInfo.isEmpty()) {
            return null;
        }
        x509Data = new X509DataImpl(keyInfo.get(0));
        return x509Data.getKey();
    }

    @Override
    public X509Data getX509Data() { return x509Data; }
}
