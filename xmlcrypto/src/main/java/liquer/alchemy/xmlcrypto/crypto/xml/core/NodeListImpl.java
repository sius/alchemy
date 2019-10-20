package liquer.alchemy.xmlcrypto.crypto.xml.core;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NodeListImpl implements NodeList {

    private final List<Node> nodes;

    public NodeListImpl() {
        this(null);
    }

    public NodeListImpl(List<Node> nodes) {
        this.nodes = nodes == null ? new ArrayList<>() : nodes;
    }

    @Override
    public Node item(int index) {
        return this.nodes.get(index);
    }

    @Override
    public int getLength() {
        return this.nodes.size();
    }
}
