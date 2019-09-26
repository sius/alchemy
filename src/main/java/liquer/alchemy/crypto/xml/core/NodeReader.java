package liquer.alchemy.crypto.xml.core;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.XmlSupport;
import liquer.alchemy.support.StringSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Safe Node reading; avoid NullPointerExceptions
 */
public abstract class NodeReader {

    /**
     * Safe reading of an Optional Node value
     *
     * @param node The Optional Node instance or Null
     * @return The Node Value or Null if node is Null or nor present
     */
    public String readNodeValue(Optional<Node> node) {
        return readNodeValue(node,null);
    }

    /**
     * Safe reading of an Optional Node value
     *
     * @param node The Optional Node instance or Null
     * @param defaultValue The defaultValue to use if node is Null or not present
     * @return The Node Value or defaultValue if node is Null or nor present
     */
    public String readNodeValue(Optional<Node> node, String defaultValue) {
        String ret = defaultValue;
        if (node != null && node.isPresent()) {
            ret = readNodeValue(node.get(),  defaultValue);
        }
        return ret;
    }

    /**
     * Safe reading of a Node value
     *
     * @param node The Node instance or Null
     * @return The Node Value or Null if node is Null
     */
    public String readNodeValue(Node node) {
        return readNodeValue(node, null);
    }

    /**
     * Safe reading of a Node value
     *
     * @param node The Node instance or Null
     * @param defaultValue The defaultValue to use if node is Null
     * @return The Node Value or defaultValue if node is Null
     */
    public String readNodeValue(Node node, String defaultValue) {
        String ret = defaultValue;
        if (node != null) {
            ret = node.getNodeValue();
        }
        return ret;
    }

    /**
     * Safe reading of a named items value
     *
     * @param node The Optional Node instance or Null
     * @param name The named items (attribute) name
     * @return The named items value
     */
    public String readNamedItem(Optional<Node> node, String name) {
        return readNamedItem(node, name, null);
    }

    /**
     * Safe reading of a named items value
     *
     * @param node The Optional Node instance or Null
     * @param name The named items (attribute) name
     * @param defaultValue The defaultValue to use if node is Null or not present
     * @return The named items value or defaultValue if node is Null or not present
     */
    public String readNamedItem(Optional<Node> node, String name, String defaultValue) {
        String ret = defaultValue;
        if (node != null && node.isPresent() && name != null) {
            ret = readNamedItem(node.get(), name, defaultValue);
        }
        return ret;
    }

    /**
     * Safe reading of a named items value
     *
     * @param node The Node instance or Null
     * @param name The named items (attribute) name
     * @return The named items value or Null if node is Null
     */
    public String readNamedItem(Node node, String name) {
        return readNamedItem(node, name, null);
    }

    /**
     * Safe reading of a named items value
     *
     * @param node The Node instance or Null
     * @param name The named items (attribute) name
     * @param defaultValue The defaultValue to use if node is Null
     * @return The named items value or defaultValue if node is Null
     */
    public String readNamedItem(Node node, String name, String defaultValue) {
        String ret = defaultValue;
        if (node != null && name != null) {
            Node tmp = node.getAttributes().getNamedItem(name);
            if (tmp != null) {
                ret = tmp.getNodeValue();
            }
        }
        return ret;
    }

    /**
     * Safe reading of node text content
     *
     * @param node The Node instance or Null
     * @return The node text content or Null id node is Null
     */
    public String readTextContent(Node node) {
        return readTextContent(node, null);
    }

    /**
     * Safe reading of node text content
     *
     * @param node The Node instance or Null
     * @param defaultValue The defaultValue to use if node is Null
     * @return The node text content or Null id node is Null
     */
    public String readTextContent(Node node, String defaultValue) {
        String ret = defaultValue;
        if (node != null) {
            ret  = node.getTextContent();
        }
        return ret;
    }

    /**
     * Safe reading of node text content
     *
     * @param node The Optional Node instance or Null
     * @return The node text content or Null if node is Null or not present
     */
    public String readTextContent(Optional<Node>node) {
        return readTextContent(node, null);
    }

    /**
     * Safe reading of node text content
     *
     * @param node The Optional Node instance or Null
     * @param defaultValue The defaultValue to use if node is Null or not present
     * @return The node text content or defaultValue if node is Null or not present
     */
    public String readTextContent(Optional<Node> node, String defaultValue) {
        String ret = defaultValue;
        if (node != null && node.isPresent()) {
            ret  = readTextContent(node.get(), defaultValue);
        }
        return ret;
    }

    /**
     * Safe reading of node elements by its qualified name
     * @param node The Optional Node instance or Null
     * @param namespaceURI The namespaceURI or DEFAULT_NS_URI if Null
     * @param elementName The elements name
     * @return a NodeList with the specified elements or an empty NodeList
     */
    public NodeList readElementsByTagNameNS(Node node, String namespaceURI, String elementName) {
        NodeList ret = new NodeListImpl();
        if (node instanceof Element && StringSupport.notNullEmptyOrBlank(elementName)) {
            final String finalNameSpaceURI = (namespaceURI == null)
                ? Identifier.DEFAULT_NS_URI
                : namespaceURI.trim();
            ret = ((Element)node).getElementsByTagNameNS(finalNameSpaceURI, elementName);
        }
        return ret;
    }

    /**
     * Safe reading of node elements by its qualified name
     * @param node The Optional Node instance or Null
     * @param namespaceURI The namespaceURI or DEFAULT_NS_URI if Null
     * @param elementName The elements name
     * @return a Stream with the specified elements or an empty Stream
     */
    public Stream<Node> readElementsStreamByTagNameNS(Node node, String namespaceURI, String elementName) {
        return XmlSupport.toStream(readElementsByTagNameNS(node, namespaceURI, elementName));
    }
}
