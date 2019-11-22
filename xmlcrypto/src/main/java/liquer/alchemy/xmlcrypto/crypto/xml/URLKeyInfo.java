package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class URLKeyInfo implements KeyInfo {

    private static final Logger LOG = LoggerFactory.getLogger(URLKeyInfo.class);

    private URL key;

    public URLKeyInfo(URL key) {
        this.key = key;
    }

    @Override
    public String getKey(List<Node> keyInfo) {
        try {
            return IOSupport.toString(key.openStream());
        } catch (IOException e) {
            LOG.error("Cannot retrieve key", e);
            return null;
        }
    }
}