package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileKeyInfo implements KeyInfo {

    private static final Logger LOG = LoggerFactory.getLogger(FileKeyInfo.class);

    private final File file;

    public FileKeyInfo(File file) {
        this.file = file;
    }

    @Override
    public String getKey(List<Node> keyInfo) {
        try {
            return IOSupport.toString(file);
        } catch (IOException e) {
            LOG.error("Cannot retrieve key", e);
            return null;
        }
    }
}
