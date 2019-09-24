package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XmlReference {
    private String uri;
    private String digestAlgorithm;
    private List<String> transforms;
    private String xpathExpression;
    private String digestValue;
    private List<PrefixNamespaceTuple> ancestorNamespaces;
    private List<String> inclusiveNamespacesPrefixList;
    private boolean emptyUri;

    public XmlReference() {
        setUri("");
        setDigestAlgorithm(null);
        setXpathExpression(null);
        setDigestValue(null);
        setTransforms(null);
        setAncestorNamespaces(null);
        setInclusiveNamespacesPrefixList(null);
        setEmptyUri(false);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm == null
            ? Identifier.SHA1
            : digestAlgorithm;
    }

    public String getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    public List<String> getTransforms() {
        return new ArrayList<>(transforms);
    }

    public void setTransforms(List<String> transforms) {
        this.transforms =
                transforms == null
                        ? Collections.singletonList(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS)
                        : new ArrayList<>(transforms);
    }

    public String getXpathExpression() {
        return xpathExpression;
    }

    public void setXpathExpression(String xpathExpression) {
        this.xpathExpression = xpathExpression;
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

    public List<String> getInclusiveNamespacesPrefixList() {
        return new ArrayList<>(inclusiveNamespacesPrefixList);
    }

    public void setInclusiveNamespacesPrefixList(List<String> inclusiveNamespacesPrefixList) {
        this.inclusiveNamespacesPrefixList =
                inclusiveNamespacesPrefixList == null
                        ? new ArrayList<>()
                        : new ArrayList<>(inclusiveNamespacesPrefixList);
    }

    public boolean isEmptyUri() {
        return emptyUri;
    }

    public void setEmptyUri(boolean emptyUri) {
        this.emptyUri = emptyUri;
    }
}
