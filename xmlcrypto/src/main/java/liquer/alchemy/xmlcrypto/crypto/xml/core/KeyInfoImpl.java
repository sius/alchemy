package liquer.alchemy.xmlcrypto.crypto.xml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.KeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.X509Data;
import org.w3c.dom.Node;

import java.util.List;

public class KeyInfoImpl implements KeyInfo {

    private final X509Data data;

    public KeyInfoImpl(X509Data data) {
        this.data = data;
    }

    @Override
    public String getKey(List<Node> keyInfo) {
        return data.getKey();
    }

    @Override
    public X509Data getX509Data() {
        return data;
    }

    @Override
    public String getKey() {
        return data.getKey();
    }
}
