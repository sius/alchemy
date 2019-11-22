package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

import liquer.alchemy.xmlcrypto.crypto.alg.Algorithm;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSupport;
import liquer.alchemy.xmlcrypto.crypto.xml.core.CharacterEncoder;
import liquer.alchemy.xmlcrypto.crypto.xml.core.PrefixNamespaceTuple;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static liquer.alchemy.xmlcrypto.support.StringSupport.isNullOrEmpty;
import static liquer.alchemy.xmlcrypto.support.StringSupport.notNullOrEmpty;

public interface CanonicalXml extends Algorithm {

    Node apply(Node node, CanonicalOptions options) throws CanonicalizationException;

    default String getName() {
        return getIdentifier();
    }

    default int compareAttribute(Node a, Node b) {
        if (isNullOrEmpty(a.getNamespaceURI()) && notNullOrEmpty(b.getNamespaceURI())) {
            return -1;
        }
        if (isNullOrEmpty(b.getNamespaceURI()) && notNullOrEmpty(a.getNamespaceURI())) {
            return 1;
        }
        String left = a.getNamespaceURI() + a.getLocalName();
        String right = b.getNamespaceURI() + b.getLocalName();

        return left.compareTo(right);
    }

    default int compareNamespace(PrefixNamespaceTuple a, PrefixNamespaceTuple b) {
        return a.prefix.equals(b.prefix) ? 0 : a.prefix.compareTo(b.prefix);
    }

    default StringBuilder buildComment(Node node) {
        StringBuilder ret = new StringBuilder();

        boolean parentIsOwnerDocument = node.getOwnerDocument().equals(node.getParentNode());
        boolean beforeDocument = false;
        boolean afterDocument = false;

        if (parentIsOwnerDocument) {
            Node nextNode = node;
            Node previousNode = node;

            while (nextNode != null) {
                if (nextNode == node.getOwnerDocument().getDocumentElement()) {
                    beforeDocument = true;
                    break;
                }
                nextNode = nextNode.getNextSibling();
            }

            while (previousNode != null) {
                if (previousNode == node.getOwnerDocument().getDocumentElement()) {
                    afterDocument = true;
                    break;
                }
                previousNode = previousNode.getPreviousSibling();
            }
        }
        return ret
            .append(afterDocument ? "\n" : "")
            .append("<!--")
            .append(CharacterEncoder.getInstance().encodeText(node.getTextContent()))
            .append("-->")
            .append((beforeDocument ? "\n" : ""));
    }

    default StringBuilder buildAttributes(Node node) {

        if (node.getNodeType() == Node.COMMENT_NODE) {
            return buildComment(node);
        }

        List<Node> attributeList = new ArrayList<>();

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attribute = attributes.item(i);
            if (!attribute.getNodeName().startsWith("xmlns")) {
                attributeList.add(attribute);
            }
        }
        attributeList.sort(this::compareAttribute);

        StringBuilder ret = new StringBuilder();
        for (Node n : attributeList) {
            XmlSupport.buildAttribute(ret,
                    " " + n.getNodeName(),
                    CharacterEncoder.getInstance().encodeAttributeValue(n.getNodeValue()));
        }
        return ret;
    }
}
