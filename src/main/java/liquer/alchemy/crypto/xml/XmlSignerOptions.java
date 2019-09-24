package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.Identifier;

import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static liquer.alchemy.crypto.xml.EOL.LF;

/**
 * The XmlSignerOptions to customize the XmlSigner behaviour
 */
public class XmlSignerOptions {

    private String idMode;
    private String signatureAlgorithm;
    private String canonicalizationAlgorithm;
    private Set<String> idAttributes;
    private List<String> implicitTransforms;
    private NamespaceContext namespaceContext;
    private EOL eol;

    public XmlSignerOptions() {
        setIdMode(null);
        setSignatureAlgorithm(Identifier.RSA_WITH_SHA1);
        setCanonicalizationAlgorithm(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS);
        setIdAttributes(null);
        setImplicitTransforms(null);
        setNamespaceContext(new NamespaceContextMap());
        setEol(LF);
    }

    public EOL getEol() {
        return eol;
    }

    public void setEol(EOL eol) {
        this.eol = eol;
    }
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getCanonicalizationAlgorithm() {
        return canonicalizationAlgorithm;
    }

    public void setCanonicalizationAlgorithm(String canonicalizationAlgorithm) {
        this.canonicalizationAlgorithm = canonicalizationAlgorithm;
    }

    public Set<String> getIdAttributes() {
        return new HashSet<>(idAttributes);
    }

    public void setIdAttributes(Set<String> idAttributes) {
        this.idAttributes
                = idAttributes == null
                ? new HashSet<>()
                : new HashSet<>(idAttributes);
    }

    public List<String> getImplicitTransforms() {
        return new ArrayList<>(implicitTransforms);
    }

    public void setImplicitTransforms(List<String> implicitTransforms) {
        this.implicitTransforms
                = implicitTransforms == null
                ? new ArrayList<>()
                : new ArrayList<>(implicitTransforms);
    }

    /**
     * A Value of "wssecurity" will create/validate id's
     * with the ws-security namespace
     *
     * @return the idMode or Null
     */
    public String getIdMode() {
        return idMode;
    }

    public void setIdMode(String idMode) {
        this.idMode = idMode;
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

}
