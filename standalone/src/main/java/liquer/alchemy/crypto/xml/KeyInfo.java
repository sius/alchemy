package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.xml.X509Data;
import liquer.alchemy.crypto.xml.XmlSupport;
import org.w3c.dom.Node;

import java.util.List;

public interface KeyInfo {

    String getKey(List<Node> keyInfo);

    default X509Data getX509Data() { return null; }

    default String getKey() {
        return getKey(null);
    }

    default String getKeyInfo(String prefix) {
        String tmp = prefix == null ? "" : prefix;
        final String finalPrefix = tmp.isEmpty() ? "" : prefix + ":";
        final String tagName = finalPrefix + "X509Data";
        return XmlSupport.buildEmptyTag(new StringBuilder(), tagName).toString();
    }
}
