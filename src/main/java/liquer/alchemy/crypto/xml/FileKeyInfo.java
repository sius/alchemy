package liquer.alchemy.crypto.xml;

import liquer.alchemy.support.IOSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileKeyInfo implements KeyInfo {

    private static Logger LOG = LogManager.getLogger(FileKeyInfo.class);

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
