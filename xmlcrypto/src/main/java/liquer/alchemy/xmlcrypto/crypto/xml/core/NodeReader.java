package liquer.alchemy.xmlcrypto.crypto.xml.core;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.support.StringSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

/**
 * Safe Node reading; avoid NullPointerExceptions
 */
public interface NodeReader {

    /**
     *
     * @param list
     * @return
     */
    default Optional<Node> readFirst(NodeList list) {
        return (list == null || list.getLength() == 0)
                ? Optional.empty()
                : Optional.of(list.item(0));
    }

    /**
     * Safe reading of Node text content
     *
     * @param node The Node instance or null
     * @param defaultValue The defaultValue to use if Node is null
     * @return The Node text content or null if Node is null
     */
    default String readTextContent(Node node, String defaultValue) {
        String ret = defaultValue;
        if (node != null) {
            ret  = node.getTextContent();
        }
        return ret;
    }

    /**
     * Safe reading of node text content
     *
     * @param node The Node instance or null
     * @return The node text content or null if node is null
     */
    default String readTextContent(Node node) {
        return readTextContent(node, null);
    }

    /**
     * Safe reading of node elements by its qualified name
     * @param node The Optional Node instance or Null
     * @param namespaceURI The namespaceURI or DEFAULT_NS_URI if Null
     * @param elementName The elements name
     * @return a NodeList with the specified elements or an empty NodeList
     */
    default NodeList readElementsByTagNameNS(Node node, String namespaceURI, String elementName) {
        NodeList ret = new NodeListImpl();
        if (node instanceof Element && StringSupport.notNullEmptyOrBlank(elementName)) {
            final String finalNameSpaceURI = (namespaceURI == null)
                    ? Identifier.DEFAULT_NS_URI
                    : namespaceURI.trim();
            ret = ((Element)node).getElementsByTagNameNS(finalNameSpaceURI, elementName);
        }
        return ret;
    }
}
