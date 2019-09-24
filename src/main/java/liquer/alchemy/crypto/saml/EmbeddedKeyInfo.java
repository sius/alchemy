package liquer.alchemy.crypto.saml;

import liquer.alchemy.crypto.KeyInfo;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import liquer.alchemy.crypto.xml.X509Data;
import org.w3c.dom.Node;

import java.util.List;

public class EmbeddedKeyInfo extends SafeNodeReader implements KeyInfo {

    private X509Data x509Data;

    @Override
    public String getKey(List<Node> keyInfo) {
        if (keyInfo == null || keyInfo.isEmpty()) {
            return null;
        }
        x509Data = new X509Data(keyInfo.get(0));
        return x509Data.getKey();
    }

    @Override
    public X509Data getX509Data() { return x509Data; }
}
