package liquer.alchemy.crypto.xml;

import liquer.alchemy.support.IOSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class URLKeyInfo implements KeyInfo {

    private static Logger LOG = LogManager.getLogger(URLKeyInfo.class);

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
