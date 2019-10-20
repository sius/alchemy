package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.core.NamespaceContextMap;
import liquer.alchemy.xmlcrypto.support.Clock;
import liquer.alchemy.xmlcrypto.support.Timer;

import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The XmlSignerOptions to customize the XmlSigner behaviour
 */
public class XmlSignerOptions {

    private String idMode;
    private String signatureAlgorithm;
    private String canonicalizationAlgorithm;
    private String digestAlgorithm;
    private Set<String> idAttributes;
    private List<String> implicitTransforms;
    private NamespaceContext namespaceContext;
    private boolean preferSelfClosingTag;

    private Timer timer;

    public XmlSignerOptions() {
        setIdMode(null);
        setSignatureAlgorithm(Identifier.RSA_WITH_SHA1);
        setCanonicalizationAlgorithm(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS);
        setDigestAlgorithm(Identifier.SHA1);
        setIdAttributes(null);
        setImplicitTransforms(null);
        setNamespaceContext(new NamespaceContextMap());
        setPreferSelfClosingTag(false);
        setTimer(Clock.set());
    }

    public XmlSignerOptions signatureAlgorithm() {
        return signatureAlgorithm(Identifier.RSA_WITH_SHA1);
    }


    public XmlSignerOptions signatureAlgorithm(String signatureAlgorithm) {
        setSignatureAlgorithm(signatureAlgorithm);
        return this;
    }

    public XmlSignerOptions canonicalizationAlgorithm() {
        return canonicalizationAlgorithm(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS);
    }

    public XmlSignerOptions canonicalizationAlgorithm(String canonicalizationAlgorithm) {
        setCanonicalizationAlgorithm(canonicalizationAlgorithm);
        return this;
    }

    public XmlSignerOptions digestAlgorithm() {
        return digestAlgorithm(Identifier.SHA1);
    }

    public XmlSignerOptions digestAlgorithm(String digestAlgorithm) {
        setDigestAlgorithm(digestAlgorithm);
        return this;
    }


    public boolean isPreferSelfClosingTag() {
        return preferSelfClosingTag;
    }

    public void setPreferSelfClosingTag(boolean preferSelfClosingTag) {
        this.preferSelfClosingTag = preferSelfClosingTag;
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

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
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

    public XmlSignerOptions idAttributes(Set<String> idAttributes) {
        setIdAttributes(idAttributes);
        return this;
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

    public XmlSignerOptions implicitTransforms(List<String> implicitTransforms) {
        setImplicitTransforms(implicitTransforms);
        return this;
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
    public XmlSignerOptions idMode(String idMode) {
        setIdMode(idMode);
        return this;
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }
    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }
    public XmlSignerOptions namespaceContext(NamespaceContext namespaceContext) {
        setNamespaceContext(namespaceContext);
        return this;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public XmlSignerOptions timer(Timer t) {
        setTimer(t);
        return this;
    }

}
