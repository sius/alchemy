package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

import liquer.alchemy.xmlcrypto.crypto.xml.core.PrefixNamespaceTuple;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanonicalOptions {

    private String defaultNamespaceURI;
    private Map<String, String> defaultNsForPrefix;
    private List<String> inclusiveNamespacesPrefixList;
    private List<PrefixNamespaceTuple> ancestorNamespaces;
    private Node signatureNode;

    public CanonicalOptions() {
        defaultNamespaceURI = "";
        defaultNsForPrefix = new HashMap<>();
        inclusiveNamespacesPrefixList = new ArrayList<>();
        ancestorNamespaces = new ArrayList<>();
        signatureNode = null;
    }

    public String getDefaultNamespaceURI() {
        return defaultNamespaceURI;
    }

    public void setDefaultNamespaceURI(String defaultNamespaceURI) {
        this.defaultNamespaceURI = defaultNamespaceURI;
    }

    public CanonicalOptions defaultNamespaceURI(String defaultNamespaceURI) {
        setDefaultNamespaceURI(defaultNamespaceURI);
        return this;
    }
    public Map<String, String> getDefaultNsForPrefix() {
        return new HashMap<>(defaultNsForPrefix);
    }
    public void setDefaultNsForPrefix(Map<String, String> defaultNsForPrefix) {
        this.defaultNsForPrefix =
                defaultNsForPrefix == null
                    ? new HashMap<>()
                    : new HashMap<>(defaultNsForPrefix);
    }

    public CanonicalOptions defaultNsForPrefix(Map<String, String> defaultNsForPrefix) {
        setDefaultNsForPrefix(defaultNsForPrefix);
        return this;
    }

    public List<String> getInclusiveNamespacesPrefixList() {
        return new ArrayList<>(inclusiveNamespacesPrefixList);
    }

    public void setInclusiveNamespacesPrefixList(List<String> inclusiveNamespacesPrefixList) {
        this.inclusiveNamespacesPrefixList =
                inclusiveNamespacesPrefixList == null
                    ? new ArrayList<>()
                    : new ArrayList<>(inclusiveNamespacesPrefixList);
    }

    public CanonicalOptions inclusiveNamespacesPrefixList(List<String> inclusiveNamespacesPrefixList) {
        setInclusiveNamespacesPrefixList(inclusiveNamespacesPrefixList);
        return this;
    }

    public List<PrefixNamespaceTuple> getAncestorNamespaces() {
        return new ArrayList<>(ancestorNamespaces);
    }

    public void setAncestorNamespaces(List<PrefixNamespaceTuple> ancestorNamespaces) {
        this.ancestorNamespaces =
                ancestorNamespaces == null
                    ? new ArrayList<>()
                    : new ArrayList<>(ancestorNamespaces);
    }

    public CanonicalOptions ancestorNamespaces(List<PrefixNamespaceTuple> ancestorNamespaces) {
        setAncestorNamespaces(ancestorNamespaces);
        return this;
    }

    public Node getSignatureNode() {
        return signatureNode;
    }

    public void setSignatureNode(Node signatureNode) {
        this.signatureNode = signatureNode;
    }

    public CanonicalOptions signatureNode(Node signatureNode) {
        setSignatureNode(signatureNode);
        return this;
    }
}
