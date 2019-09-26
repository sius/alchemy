package liquer.alchemy.crypto.xml;

import liquer.alchemy.alkahest.Func.Func3;
import liquer.alchemy.alkahest.Func.Func2;
import liquer.alchemy.crypto.xml.core.NodeListImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static liquer.alchemy.support.StringSupport.isNullOrEmpty;

/**
 *
 */
public class XPathSupport {
    private static final Logger LOG = LogManager.getLogger(XPathSupport.class);
    private static final XPathFactory XPATH_FACTORY;

    static {
        XPATH_FACTORY = XPathFactory.newInstance();

        try {
            XPATH_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (XPathFactoryConfigurationException e) {
            LOG.warn(e);
        }
    }

    private static XPath newXPath() {
        return newXPath(null);
    }
    private static XPath newXPath(NamespaceContext namespaceContext) {
        final XPath ret = XPATH_FACTORY.newXPath();
        if (namespaceContext != null) {
            ret.setNamespaceContext(namespaceContext);
        }
        return ret;
    }

    public static Func2<Node, String, NodeList> getSelectNodeListClosure(NamespaceContext namespaceContext) {
        Func3<NamespaceContext, Node, String, NodeList> ret = (nsCtx, doc, str) -> select(doc, str, nsCtx);
        return ret.apply(namespaceContext);
    }

    public static Func2<Node, String, Stream<Node>> getSelectStreamClosure(NamespaceContext namespaceContext) {
        Func3<NamespaceContext, Node, String, Stream<Node>> ret = (nsCtx, doc, str) -> XmlSupport.toStream(select(doc, str, nsCtx));
        return ret.apply(namespaceContext);
    }

    public static NodeList select(Node node, String xpathExpression) {
        return select(node, xpathExpression, null);
    }

    public static NodeList select(Node node, String xpathExpression, NamespaceContext namespaceContext) {
        try {
            Object found = newXPath(namespaceContext).compile(xpathExpression).evaluate(node, XPathConstants.NODESET);
            return (found instanceof NodeList) ? (NodeList)found : new NodeListImpl();
        } catch (XPathExpressionException e) {
            throw new RuntimeException(new XPathExpressionException("could not find xpath " + xpathExpression));
        }
    }

    public static Node selectNode(Node node, String xpathExpression) {
        return selectNode(node, xpathExpression, null);
    }

    public static Node selectNode(Node node, String xpathExpression, NamespaceContext namespaceContext) {
        try {
            Object found = newXPath(namespaceContext).compile(xpathExpression).evaluate(node, XPathConstants.NODE);
            return (found instanceof Node) ? (Node)found : null;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(new XPathExpressionException("could not find xpath " + xpathExpression));
        }
    }

    public static Node selectFirst(Node node, String xpathExpression) {
        return selectFirst(node, xpathExpression, null);
    }

    public static Node selectFirst(Node node, String xpathExpression, NamespaceContext namespaceContext) {
        NodeList selection = select(node, xpathExpression, namespaceContext);
        if (selection.getLength() > 0) {
            return selection.item(0);
        }
        return null;
    }
    public static Node deleteChild(Node node, String xpathExpression) {
        return deleteChild(node, xpathExpression, null);
    }

    public static Node deleteChild(Node node, String xpathExpression, NamespaceContext namespaceContext) {
        Object found;
        try {
            found = newXPath(namespaceContext).compile(xpathExpression).evaluate(node, XPathConstants.NODE);
            if (found instanceof Node) {
                Node child = (Node)found;
                Node parent = child.getParentNode();
                if (parent != null) {
                    parent.removeChild(child);
                }
            }
            return node;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(new XPathExpressionException(String.format("could not find xpath %1$s", xpathExpression)));
        }

    }

    public static NodeList findNodeChildren(Node node, String localName) {
        return findNodeChildren(node, localName, null);
    }

    public static NodeList findNodeChildren(Node node, String localName, String namespace) {
        List<Node> nodes = new ArrayList<>();
        node = node.isEqualNode(node.getOwnerDocument())
                ? node.getOwnerDocument().getDocumentElement()
                : node;
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                String childLocalName = child.getLocalName();
                if ((childLocalName != null && childLocalName.equals(localName)) && (namespace == null || namespace.isEmpty() || child.getNamespaceURI().equals(namespace))) {
                    nodes.add(child);
                }
            }
        }
        return new NodeListImpl(nodes);
    }

    public static Node findAttribute(Node node, String localName) {
       return findAttribute(node, localName, null);
    }

    public static Node findAttribute(Node node, String localName, String namespace) {
        if (node == null) {
            return null;
        }
        NamedNodeMap namedNodeMap = node.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node attribute = namedNodeMap.item(i);

            if (equalsExplicitly(attribute, localName, namespace) || equalsImplicitly(attribute, localName, namespace, node)) {
                return attribute;
            }
        }
        return null;
    }

    private static boolean equalsExplicitly(Node attr, String localName, String namespace) {
        return attr.getLocalName().equals(localName) && (isNullOrEmpty(namespace) || attr.getNamespaceURI().equals(namespace));
    }

    private static boolean equalsImplicitly(Node attr, String localName, String namespace, Node node) {
        return attr.getLocalName().equals(localName) && ((isNullOrEmpty(attr.getNamespaceURI()) && node.getNamespaceURI().equals(namespace)) || isNullOrEmpty(namespace));
    }
}


