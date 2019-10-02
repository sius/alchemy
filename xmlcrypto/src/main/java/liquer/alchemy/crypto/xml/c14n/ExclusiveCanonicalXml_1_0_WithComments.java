package liquer.alchemy.crypto.xml.c14n;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.core.CharacterEncoder;
import liquer.alchemy.crypto.xml.core.PrefixNamespaceTuple;
import liquer.alchemy.crypto.xml.XPathSupport;
import liquer.alchemy.crypto.xml.XmlSupport;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static liquer.alchemy.alembic.StringSupport.isNullOrEmpty;
import static liquer.alchemy.alembic.StringSupport.notNullOrEmpty;

/**
 * http://www.w3.org/TR/xml-exc-c14n/
 */
public class ExclusiveCanonicalXml_1_0_WithComments implements CanonicalXml {

    @Override
    public String getIdentifier() { return Identifier.EXCLUSIVE_CANONICAL_XML_1_0_WITH_COMMENTS; }

    /**
     * Build the exclusive C14n (canonicalization) string
     *
     * @param node The node to stringify
     * @param options The C14nOptions
     * @return The C14n string of the node
     */
    @Override
    public Node apply(Node node, CanonicalOptions options) {
        final CanonicalOptions o = options == null ? new CanonicalOptions() : options;

        List<String> inclusiveNamespacesPrefixList = options.getInclusiveNamespacesPrefixList();
        List<PrefixNamespaceTuple> ancestorNamespaces = options.getAncestorNamespaces();

        /*
         * If the inclusiveNamespacesPrefixList has not been explicitly provided then look it up in CanonicalizationMethod/InclusiveNamespaces
         */
        if (inclusiveNamespacesPrefixList.size() == 0) {
            NodeList canonicalizationMethod = XPathSupport.findNodeChildren(node, "CanonicalizationMethod");
            if (canonicalizationMethod.getLength() > 0) {
                NodeList inclusiveNamespaces = XPathSupport.findNodeChildren(canonicalizationMethod.item(0), "InclusiveNamespaces");
                if (inclusiveNamespaces.getLength() > 0) {
                    Node attr = inclusiveNamespaces.item(0).getAttributes().getNamedItem("PrefixList");
                    if (attr != null) {
                        inclusiveNamespacesPrefixList = Arrays.asList(attr.getNodeValue().split(" "));
                    }

                }
            }
        }

        /*
         * If you have a PrefixList then use it and the ancestors to add the necessary namespaces
         */
        if (inclusiveNamespacesPrefixList != null) {
            inclusiveNamespacesPrefixList.forEach(prefix -> {
                if (!ancestorNamespaces.isEmpty()) {
                    ancestorNamespaces.forEach(ancestorNamespace -> {
                        if (prefix.equals(ancestorNamespace.prefix)) {
                            Element elem = (Element) node;
                            elem.getAttributeNS(
                                    "http://www.w3.org/2000/xmlns/",
                                    "xmlns:" + ancestorNamespace.namespaceURI);
                        }
                    });
                }
            });
        }
        StringBuilder res = buildRecursively(
                node,
                new ArrayList<>(),
                o.getDefaultNamespaceURI(),
                o.getDefaultNsForPrefix(),
                o.getInclusiveNamespacesPrefixList());

        return XmlSupport.toDocument(res.toString());
    }

    private boolean missingPrefixInScope(List<PrefixNamespaceTuple> prefixesInScope, String prefix, String namespaceURI) {
        for (PrefixNamespaceTuple p : prefixesInScope) {
            if (p.prefix.equals(prefix) && p.namespaceURI.equals(namespaceURI)) {
                return false;
            }
        }
        return true;
    }

    private StringBuilder buildRecursively(Node node, List<PrefixNamespaceTuple> prefixesInScope, String defaultNamespaceURI, Map<String, String> defaultNsForPrefix, List<String> inclusiveNamespacesPrefixList) {

        switch (node.getNodeType()) {
            case Node.COMMENT_NODE: return buildComment(node);
            case Node.TEXT_NODE: return new StringBuilder(CharacterEncoder.getInstance().encodeText(node.getTextContent()));
            default: break;
        }

        String nodeTagName = node.getNodeName();
        NamespacesTuple namespaces = this.buildNamespaces(node, prefixesInScope, defaultNamespaceURI, defaultNsForPrefix, inclusiveNamespacesPrefixList);
        StringBuilder ret = new StringBuilder("<")
            .append(nodeTagName)
            .append(namespaces.builder)
            .append(buildAttributes(node))
            .append('>');

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            List<PrefixNamespaceTuple> prefixesInScopeCopy = new ArrayList<>(prefixesInScope);
            ret.append(
                this.buildRecursively(
                    childNodes.item(i),
                    prefixesInScopeCopy,
                    namespaces.newDefaultNamespaceURI,
                    defaultNsForPrefix,
                    inclusiveNamespacesPrefixList));
        }
        XmlSupport.buildEndTag(ret, nodeTagName);
        return ret;
    }

    /*
     * Create a Tuple with the string of all namespace declarations that should appear on this element
     *
     * @param node The node to stringify
     * @param prefixesInScope The prefixes defined on this node parents which are a part of the output set
     * @param defaultNamespaceURI The current default namespace
     * @param defaultNsForPrefix
     * @param inclusiveNamespacesPrefixList
     * @return
     */
    private NamespacesTuple buildNamespaces(Node node, List<PrefixNamespaceTuple> prefixesInScope, String defaultNamespaceURI, Map<String, String> defaultNsForPrefix, List<String> inclusiveNamespacesPrefixList) {
        StringBuilder builder = new StringBuilder();
        String newDefaultNamespaceURI = defaultNamespaceURI;
        final String nodeNamespaceURI = node.getNamespaceURI();
        String currentNamespaceURI = isNullOrEmpty(node.getNamespaceURI()) ? "" : nodeNamespaceURI;

        List<PrefixNamespaceTuple> nsTupleListToBuild = new ArrayList<>();

        String nodePrefix = node.getPrefix();

        // handel the namespace of the node itself
        if (notNullOrEmpty(nodePrefix)) {
            String effectiveNamespaceURI = notNullOrEmpty(nodeNamespaceURI) ? nodeNamespaceURI : defaultNsForPrefix.get(nodePrefix);
            if (missingPrefixInScope(prefixesInScope, nodePrefix, effectiveNamespaceURI)) {
                nsTupleListToBuild.add(new PrefixNamespaceTuple(nodePrefix, effectiveNamespaceURI));
                prefixesInScope.add(new PrefixNamespaceTuple(nodePrefix, effectiveNamespaceURI));
            }
        } else if (!defaultNamespaceURI.equals(currentNamespaceURI)) {
            newDefaultNamespaceURI = nodeNamespaceURI;
            XmlSupport.buildAttribute(builder, " xmlns", newDefaultNamespaceURI);
        }

        // handle the attributes namespace
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attr = attributes.item(i);

            if (notNullOrEmpty(attr.getPrefix())) {

                final String attrLocalName = attr.getLocalName();
                final String attrPrefix = attr.getPrefix();
                final String attrNamespaceURI = attr.getNamespaceURI();

                // handle all prefixed attributes
                // that are included in the prefix list and
                // where the prefix is not defined already
                if (missingPrefixInScope(prefixesInScope, attrLocalName, attr.getNodeValue()) && inclusiveNamespacesPrefixList.contains(attrLocalName)) {
                    PrefixNamespaceTuple t1 = new PrefixNamespaceTuple(attrLocalName, attr.getNodeValue());
                    nsTupleListToBuild.add(t1);
                    prefixesInScope.add(t1);
                }

                // handle all prefixed attributes
                // that are not xmlns definitions and
                // where the prefix is not defined already
                if (missingPrefixInScope(prefixesInScope, attrPrefix, attrNamespaceURI) && !attrPrefix.equals("xmlns") && !attrPrefix.equals("xml")) {
                    PrefixNamespaceTuple t2 = new PrefixNamespaceTuple(attrPrefix, attrNamespaceURI);
                    nsTupleListToBuild.add(t2);
                    prefixesInScope.add(t2);
                }
            }
        }
        nsTupleListToBuild.sort(this::compareNamespace);

        for (PrefixNamespaceTuple t : nsTupleListToBuild) {
            XmlSupport.buildAttribute(builder, " xmlns:" + t.prefix, t.namespaceURI);
        }

        return new NamespacesTuple(builder, newDefaultNamespaceURI);
    }
}


