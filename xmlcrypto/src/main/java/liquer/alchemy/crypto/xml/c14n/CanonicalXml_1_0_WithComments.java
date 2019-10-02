package liquer.alchemy.crypto.xml.c14n;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.xml.core.CharacterEncoder;
import liquer.alchemy.crypto.xml.core.PrefixNamespaceTuple;
import liquer.alchemy.crypto.xml.XmlSupport;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

import static liquer.alchemy.alembic.StringSupport.isNullOrEmpty;
import static liquer.alchemy.alembic.StringSupport.notNullOrEmpty;

/**
 * https://www.w3.org/TR/2001/REC-xml-c14n-20010315
 */
public class CanonicalXml_1_0_WithComments implements CanonicalXml {

    /**
     * Build the exclusive C14n (canonicalization) string
     *
     * @param node The node to stringify
     * @param options The C14n options
     * @return The C14n string of the node
     */
    @Override
    public Node apply(Node node, CanonicalOptions options) {
        final CanonicalOptions o = options == null ? new CanonicalOptions() : options;

        return XmlSupport.toDocument(buildRecursively(
            node,
            new HashSet<>(),
            o.getDefaultNamespaceURI(),
            o.getDefaultNsForPrefix(),
            o.getAncestorNamespaces())
                .toString());
    }

    @Override
    public String getIdentifier() {
        return Identifier.CANONICAL_XML_1_0_WITH_COMMENTS;
    }

    private StringBuilder buildRecursively(Node node, Set<String> prefixesInScope, String defaultNamespaceURI, Map<String, String> defaultNsForPrefix, List<PrefixNamespaceTuple> ancestorNamespaces) {

        if (node.getNodeType() == Node.COMMENT_NODE) {
            return buildComment(node);
        }

        if (notNullOrEmpty(node.getTextContent())) {
            return new StringBuilder(CharacterEncoder.getInstance().encodeText(node.getTextContent()));
        }

        String nodeTagName = node.getNodeName();
        NamespacesTuple namespaces = buildNamespaces(node, prefixesInScope, defaultNamespaceURI, defaultNsForPrefix, ancestorNamespaces);
        StringBuilder ret = new StringBuilder("<")
            .append(nodeTagName)
            .append(namespaces.builder)
            .append(buildAttributes(node))
            .append('>');

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Set<String> prefixesInScopeCopy = new HashSet<>(prefixesInScope);
            ret.append(
                this.buildRecursively(
                    childNodes.item(i),
                    prefixesInScopeCopy,
                    namespaces.newDefaultNamespaceURI,
                    defaultNsForPrefix,
                    new ArrayList<>()));
        }
        return XmlSupport.buildEndTag(ret, nodeTagName);
    }

    /*
     * Create a Tuple with the string of all namespace declarations that should appear on this element
     *
     * @param node The node to stringify
     * @param prefixesInScope The prefixes defined on this node parents which are a part of the output set
     * @param defaultNamespaceURI The current default namespace
     * @param defaultNsForPrefix
     * @param inclusiveNamespacesPrefixList
     * @param ancestorNamespaces - Import ancestor namespaces if it is specified
     * @return
     */
    private NamespacesTuple buildNamespaces(Node node, Set<String> prefixesInScope, String defaultNamespaceURI, Map<String, String> defaultNsForPrefix, List<PrefixNamespaceTuple> ancestorNamespaces) {
        StringBuilder builder = new StringBuilder();
        String newDefaultNamespaceURI = defaultNamespaceURI;
        final String nodeNamespaceURI = node.getNamespaceURI();
        String currentNamespaceURI = isNullOrEmpty(node.getNamespaceURI()) ? "" : nodeNamespaceURI;

        List<PrefixNamespaceTuple> nsTupleListToBuild = new ArrayList<>();

        String nodePrefix = node.getPrefix();

        // handel the namespace of the node itself
        if (notNullOrEmpty(nodePrefix)) {
            String effectiveNamespaceURI = notNullOrEmpty(nodeNamespaceURI) ? nodeNamespaceURI : defaultNsForPrefix.get(nodePrefix);
            if (!prefixesInScope.contains(nodePrefix)) {
                nsTupleListToBuild.add(new PrefixNamespaceTuple(nodePrefix, effectiveNamespaceURI));
                prefixesInScope.add(nodePrefix);
            }
        } else if (!defaultNamespaceURI.equals(currentNamespaceURI)) {
            newDefaultNamespaceURI = nodeNamespaceURI;
            XmlSupport.buildAttribute(builder, "xmlns", newDefaultNamespaceURI);
        }

        // handle the attributes namespace
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attr = attributes.item(i);
            final String attrPrefix = attr.getPrefix();

            if (notNullOrEmpty(attrPrefix)) {

                final String attrLocalName = attr.getLocalName();
                final String attrNamespaceURI = attr.getNamespaceURI();

                // handle all prefixed attributes
                // that are included in the prefix list and
                // where the prefix is not defined already
                if (!prefixesInScope.contains(attrLocalName)) {
                    nsTupleListToBuild.add(new PrefixNamespaceTuple(attrLocalName, attr.getNodeValue()));
                    prefixesInScope.add(attrLocalName);
                }

                // handle all prefixed attributes
                // that are not xmlns definitions and
                // where the prefix is not defined already
                if (!prefixesInScope.contains(attrPrefix) && !attrPrefix.equals("xmlns") && !attrPrefix.equals("xml")) {
                    nsTupleListToBuild.add(new PrefixNamespaceTuple(attrPrefix, attrNamespaceURI));
                    prefixesInScope.add(attrPrefix);
                }
            }
        }

        if(ancestorNamespaces != null && ancestorNamespaces.size() > 0){
            // Remove namespaces which are already present in nsListToRender
            for (PrefixNamespaceTuple pnt1 : ancestorNamespaces){

                boolean alreadyListed = false;
                for (PrefixNamespaceTuple pnt2 : nsTupleListToBuild) {
                    alreadyListed = (pnt2.prefix.equals(pnt1.prefix) && pnt2.namespaceURI.equals(pnt1.namespaceURI));
                }

                if(!alreadyListed){
                    nsTupleListToBuild.add(pnt1);
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


