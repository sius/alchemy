package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.alg.HashAlgorithm;
import liquer.alchemy.xmlcrypto.crypto.alg.SignatureAlgorithm;
import liquer.alchemy.xmlcrypto.crypto.xml.c14n.CanonicalOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.c14n.CanonicalXml;
import liquer.alchemy.xmlcrypto.crypto.xml.c14n.CanonicalizationException;
import liquer.alchemy.xmlcrypto.crypto.xml.core.*;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Assertion;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.DefaultNamespaceContextMap;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Signature;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.SAXIdAttributeReader;
import liquer.alchemy.xmlcrypto.support.StringSupport;
import liquer.alchemy.xmlcrypto.support.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.*;
import java.util.function.BiFunction;

public final class XmlSigner extends NodeReader {

    private static final Logger LOG = LogManager.getLogger(XmlSigner.class);

    private final String idMode;
    private final XmlSignerOptions options;

    private final NamespaceContext namespaceContext;
    private final BiFunction<Node, String, NodeList> select;
    private final BiFunction<Node, String, Node> selectNode;

    private final boolean preferSelfClosingTag;

    private int id;
    private String signingKey;
    private KeyInfo keyInfoProvider;
    private String signedXml;
    private String signatureXml;
    private Node signatureNode;
    private String signatureValue;
    private String originalXmlWithIds;

    private List<String> errors;
    private List<Node> keyInfo;
    private Set<String> idAttributes;
    private List<String> implicitTransforms;
    private List<XmlReference> references;
    private String signatureAlgorithm;
    private String canonicalizationAlgorithm;
    private Signature signature;
    private Timer timer;

    /**
     * Xml signer implementation
     *
     * Note: to sign (or verify) an Xml you must call the setKeyInfoProvider instance method
     * with the private (public) KeyInfo
     */
    public XmlSigner() {
        this(new XmlSignerOptions(), null);
    }

    /**
     * Xml signer implementation
     *
     * Note: to sign (or verify) an Xml you must call the setKeyInfoProvider instance method
     * with the private (public) KeyInfo
     * @param options the signer configurations
     */
    public XmlSigner(XmlSignerOptions options) { this(options, null); }
    /**
     * Xml signer implementation
     *
     * @param options the signer configurations
     * @param options the private or public KeyInfo
     */
    public XmlSigner(XmlSignerOptions options, KeyInfo keyInfoProvider) {
        this.options = (options == null) ? new XmlSignerOptions() : options;

        this.namespaceContext = this.options.getNamespaceContext();
        this.select = XPathSupport.getSelectNodeListClosure(this.namespaceContext);
        this.selectNode = XPathSupport.getSelectNodeClosure(this.namespaceContext);
        this.idMode = this.options.getIdMode();
        this.references = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.preferSelfClosingTag = this.options.isPreferSelfClosingTag();

        this.id = 0;
        this.signingKey = null;
        this.signedXml = "";
        this.signatureXml = "";
        this.signatureNode = null;
        this.signatureValue = "";
        this.originalXmlWithIds = "";
        this.signatureAlgorithm = this.options.getSignatureAlgorithm();
        this.canonicalizationAlgorithm = this.options.getCanonicalizationAlgorithm();
        this.keyInfoProvider = keyInfoProvider;
        this.keyInfo = null;
        this.idAttributes = new HashSet<>(Arrays.asList("ID", "Id", "id"));
        if (this.options.getIdAttributes() != null) {
            this.idAttributes.addAll(this.options.getIdAttributes());
        }
        this.implicitTransforms = this.options.getImplicitTransforms();
        this.timer = this.options.getTimer();
    }

    public List<String> getErrors() { return new ArrayList<>(errors); }

    public String getSignatureXml() {
        return this.signatureXml;
    }

    public String getOriginalXmlWithIds() {
        return this.originalXmlWithIds;
    }

    public String getSignedXml() {
        return this.signedXml;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
    public KeyInfo getKeyInfoProvider() {
        return keyInfoProvider;
    }

    public void setKeyInfoProvider(KeyInfo keyInfoProvider) {
        this.keyInfoProvider = keyInfoProvider;
    }

    public Signature getSignature() {
        return signature;
    }

    public void loadSignature(Signature signature) {
        if (signature == null) {
            throw new IllegalArgumentException("Argument 'signature' cannot be null");
        }
        try {
            this.signature = signature;
            this.signatureXml = signature.toString();
            this.signatureNode = XmlSupport.toWrappedDocument(
                "Tmp",
                "xmlns=\"" + Identifier.DEFAULT_NS_URI + "\"",
                this.signatureXml).getDocumentElement().getFirstChild();
            initSignatureValues(this.signature);
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }
    public void loadSignature(String signatureXml) {
        if (signatureXml == null) {
            throw new IllegalArgumentException("Argument 'signatureXml' cannot be null");
        }
        try {
            this.signatureNode = XmlSupport.toWrappedDocument(
                "Tmp",
                "xmlns=\"" + Identifier.DEFAULT_NS_URI + "\"",
                signatureXml).getDocumentElement().getFirstChild();
            this.signatureXml = signatureXml;
            this.signature = AssertionFactory.newSignatureBuilder(this.signatureXml, this.namespaceContext);
            initSignatureValues(this.signature);
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    public void loadSignature(Node signatureNode) {
        if (signatureNode == null) {
            throw new IllegalArgumentException("Argument 'signatureNode' cannot be null");
        }
        this.signatureNode = signatureNode;
        this.signatureXml = XmlSupport.stringify(signatureNode);
        this.signature = AssertionFactory.newSignatureBuilder(this.signatureXml, this.namespaceContext);
        initSignatureValues(this.signature);
    }

    private void initSignatureValues(Signature signature) {
        canonicalizationAlgorithm = signature.getCanonicalizationAlgorithm();
        signatureAlgorithm = signature.getSignatureAlgorithm();
        references.clear();
        references.addAll(signature.getReferences());
        signatureValue = signature.getSignatureValue();

        if (keyInfoProvider == null) {
            keyInfoProvider = signature.getKeyInfo();
        }

        signingKey = keyInfoProvider.getKey(this.keyInfo);

        if (signingKey == null) {
            signingKey = signature.getKeyInfo().getKey();
        }
    }

    /**
     * Validates the references and signature of a generic XML Document
     *
     * maximum attack surface
     *
     * @param xml
     * @return
     */
    public ValidationResult validateSignature(String xml) {
        signedXml = xml;
        errors = new ArrayList<>();
        try {
            if (keyInfoProvider == null) {
                throw new NullPointerException("Cannot validate signature: no key info resolver was provided");
            }

            signingKey = keyInfoProvider.getKey(keyInfo);

            if (signingKey == null) {
                throw new NullPointerException("Could not resolve key info: " + keyInfo);
            }

            final Document doc = XmlSupport.toDocument(xml);
            final boolean validSignature =
                validateReferences(doc, xml) && validateSignatureValue(doc);

            return new XmlValidationResult(
                validSignature,
                Collections.unmodifiableList(this.errors));

        } catch (SignatureException
            | NoSuchAlgorithmException
            | InvalidKeyException
            | NoSuchProviderException
            | SAXException
            | IOException
            | NullPointerException
            | CanonicalizationException e) {
            LOG.error(e.getMessage(), e);
            errors.add(e.getMessage());
            return new XmlValidationResult(
                false,
                Collections.unmodifiableList(this.errors));
        }
    }

    /**
     * Validates the references and signature of a Assertion Document
     * with an enveloped Signature element.
     *
     * small attack surface
     *
     * @param assertion
     * @return
     */
    public ValidationResult validateAssertion(Assertion assertion) {
        errors = new ArrayList<>();
        try {
            signature = assertion.getSignature();
            initSignatureValues(signature);
            final XmlReference assertionReference =
                signature.getAssertionReference();
            final Document assertionDoc =
                XmlSupport.toDocument(assertion.toString());


            CanonicalOptions options =
                new CanonicalOptions()
                .inclusiveNamespacesPrefixList(assertionReference.getInclusiveNamespacesPrefixList())
                .ancestorNamespaces(assertionReference.getAncestorNamespaces());

            String canonicalXml =
                this.getCanonicalXml(
                    assertionReference.getTransforms(),
                    assertionDoc.getDocumentElement(),
                    options);

            HashAlgorithm hash = this.findHashAlgorithm(assertionReference.getDigestAlgorithm());
            String digest = hash.hash(canonicalXml, null);

            boolean validDigest = digest.equals(assertionReference.getDigestValue());

            if (!validDigest) {
                this.errors.add(
                    "invalid signature: for uri " + assertionReference.getUri() +
                    " calculated digest is " + digest +
                    " but the xml to validate supplies digest " + assertionReference.getDigestValue());

            }

            final boolean validSignature =
                validDigest && validateSignatureValue(assertionDoc);
            return new XmlValidationResult(
                validSignature,
                Collections.unmodifiableList(this.errors));

        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | IOException | SAXException | CanonicalizationException e) {
            LOG.error(e.getMessage(), e);
            this.errors.add(e.getMessage());
            return new XmlValidationResult(
                false,
                Collections.unmodifiableList(this.errors));
        }
    }

    public void addReference(String xpathExpression) {
        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(xpathExpression);
        this.references.add(ref);
    }

    public void addReference(String xpathExpression, List<String> transforms) {
        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(xpathExpression);
        ref.setTransforms(transforms);
        this.references.add(ref);
    }

    public void addReference(String xpathExpression, List<String> transforms, String digestAlgorithm) {
        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(xpathExpression);
        ref.setTransforms(transforms);
        ref.setDigestAlgorithm(digestAlgorithm);
        this.references.add(ref);
    }

    public void addReference(
            String xpathExpression,
            List<String> transforms,
            String digestAlgorithm,
            String uri,
            String digestValue,
            List<String> inclusiveNamespacesPrefixList,
            boolean isEmptyUri) {

        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(xpathExpression);
        ref.setTransforms(transforms);
        ref.setDigestAlgorithm(digestAlgorithm);
        ref.setUri(uri);
        ref.setDigestValue(digestValue);
        ref.setInclusiveNamespacesPrefixList(inclusiveNamespacesPrefixList);
        ref.setEmptyUri(isEmptyUri);

        this.references.add(ref);
    }

    public void addReference(XmlReference ref) {
        final XmlReference newRef = ref == null ? new XmlReference() : ref;
        this.references.add(newRef);
    }

    /**
     * @param xml
     */
    public void computeSignature(String xml) {
        computeSignature(xml, new char[0], null);
    }

    /**
     * @param xml The xml String
     * @param passphrase the passphrase to decrypt an encrypted key,
     *                   an empty char array signifies an unencrypted key
     *
     * @param xml
     * @param passphrase
     */
    public void computeSignature(String xml, char[] passphrase) {
        computeSignature(xml, passphrase, null);
    }
    /**
     * Compute the signature of the given xml (using the already defined settings)
     *
     * @param xml The xml String
     * @param passphrase the passphrase to decrypt an encrypted key,
     *                   an empty char array signifies an unencrypted key
     * @param options The signature options
     */
    public void computeSignature(String xml, char[] passphrase, SignatureOptions options) {
        try {
            final SignatureOptions finalOptions = options == null ? new SignatureOptions() : options;

            final Document doc = XmlSupport.toDocument(xml);
            final List<String> signatureAttrs = new ArrayList<>();

            final String prefix = finalOptions.getPrefix();
            final Map<String, String> attrs = finalOptions.getAttrs();
            final Location location = finalOptions.getLocation();
            final Map<String, String> existingPrefixes = finalOptions.getExistingPrefixes();
            String xmlnsAttr = "xmlns";
            final String currentPrefix;

            // automatic insertion of `:`
            if (StringSupport.notNullOrEmpty(prefix)) {
                xmlnsAttr += ":" + prefix;
                currentPrefix = prefix + ":";
            } else {
                currentPrefix = "";
            }

            for(Map.Entry<String, String> entry : attrs.entrySet()) {
                String name = entry.getKey();
                if ("xmlns".equals(name) && !xmlnsAttr.equals(name)) {
                    signatureAttrs.add(XmlSupport.buildAttribute(name, entry.getValue()).toString());
                }
            };

            // add the xml namespace attribute
            signatureAttrs.add(XmlSupport.buildAttribute(xmlnsAttr, Identifier.DEFAULT_NS_URI).toString());

            final StringBuilder signatureXml = new StringBuilder();
            signatureXml.append('<');
            signatureXml.append(currentPrefix);
            signatureXml.append("Signature ");
            signatureXml.append(String.join(" ", signatureAttrs));
            signatureXml.append('>');

            final String signedInfo = createSignedInfo(doc, prefix);
            signatureXml.append(signedInfo);
            signatureXml.append(getKeyInfo(prefix));
            signatureXml.append("</");
            signatureXml.append(currentPrefix);
            signatureXml.append("Signature");
            signatureXml.append('>');

            this.originalXmlWithIds = XmlSupport.stringify(doc);

            final StringBuilder existingPrefixesBuilder = new StringBuilder();

            existingPrefixes.forEach((key, value) -> {
                XmlSupport.buildAttribute(existingPrefixesBuilder, "xmlns:" + key, value);
            });

            // A trick to remove the namespaces that already exist in the xml
            // This only works if the prefix and namespace match with those in the xml
            final Document wrappedDoc = XmlSupport.toWrappedDocument("Dummy", existingPrefixesBuilder.toString(), signatureXml.toString());
            final Node firstChild = wrappedDoc.getDocumentElement().getFirstChild();
            final Node signatureDoc = doc.adoptNode(firstChild);

            Node referenceNode = this.selectNode.apply(doc, location.getReference());
            if (referenceNode == null) {
                throw new IllegalStateException(
                    "The following xpath cannot be used because it was not found: " +
                        location.getReference());
            }

            switch (location.getAction()) {
                case append:
                    referenceNode.appendChild(signatureDoc);
                    break;
                case prepend:
                    referenceNode.insertBefore(signatureDoc, referenceNode.getFirstChild());
                    break;
                case before:
                    referenceNode.getParentNode().insertBefore(signatureDoc, referenceNode);
                    break;
                case after:
                    referenceNode.getParentNode().insertBefore(signatureDoc, referenceNode.getNextSibling());
                    break;
            }

            this.signatureNode = signatureDoc;
            this.calculateSignatureValue(doc, passphrase);
            final NodeList signedInfoNodes = XPathSupport.findNodeChildren(this.signatureNode, "SignedInfo");
            if (signedInfoNodes.getLength() == 0) {
                throw new RuntimeException("Could not find SignedInfo element in the message");
            }

            final Node signedInfoNode = signedInfoNodes.item(0);
            signatureDoc.insertBefore(doc.adoptNode(this.createSignature(prefix)), signedInfoNode.getNextSibling());

            this.signatureXml = XmlSupport.stringify(signatureDoc);
            this.signedXml = XmlSupport.stringify(doc);
        } catch (NoSuchAlgorithmException
            | SignatureException
            | InvalidKeyException
            | NoSuchProviderException
            | IOException
            | SAXException
            | CanonicalizationException e) {
            LOG.error(e.getMessage(), e);
            this.errors.add(e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /*
     * Extract ancestor namespaces in order to import it to root of document subset
     * which is being canonicalized for non-exclusive c14n.
     *
     * @param doc - The Document node
     * @param docSubsetXpath - xpath query to get document subset being canonicalized
     * @return The PrefixNamespaceTuple list, i.e. [{prefix: "saml", namespaceURI: "urn:oasis:names:tc:SAML:2.0:assertion"}]
     */
    private List<PrefixNamespaceTuple> findAncestorNamespaces(Document doc, String docSubsetXpath){
        NodeList docSubset = select.apply(doc, docSubsetXpath);

        if (docSubset.getLength() == 0) {
            return new ArrayList<>();
        }

        // Remove duplicate on ancestor namespace
        List<PrefixNamespaceTuple> ancestorNs = collectAncestorNamespaces(docSubset.item(0), null);
        List<PrefixNamespaceTuple> ancestorNsWithoutDuplicate = new ArrayList<>();

        for(PrefixNamespaceTuple a : ancestorNs) {
            boolean notOnTheList = true;

            for(PrefixNamespaceTuple d : ancestorNsWithoutDuplicate){
                if(d.prefix.equals(a.prefix)){
                    notOnTheList = false;
                    break;
                }
            }

            if(notOnTheList){
                ancestorNsWithoutDuplicate.add(a);
            }

        }

        // Remove namespaces which are already declared in the subset with the same prefix
        List<PrefixNamespaceTuple> ret = new ArrayList<>();
        NamedNodeMap subsetAttributes = docSubset.item(0).getAttributes();
        for (PrefixNamespaceTuple d : ancestorNsWithoutDuplicate){
            boolean isUnique = true;
            for(int k = 0; k < subsetAttributes.getLength(); k++){
                String nodeName = subsetAttributes.item(k).getNodeName();
                if (!nodeName.startsWith("xmlns:")) {
                    continue;
                }
                String prefix = nodeName.replaceFirst("xmlns:", "");
                if (d.prefix.equals(prefix)) {
                    isUnique = false;
                    break;
                }
            }

            if(isUnique){
                ret.add(d);
            }
        }

        return ret;
    }

    private List<PrefixNamespaceTuple> collectAncestorNamespaces(Node node, List<PrefixNamespaceTuple> prefixNamespaceTupleList){

        List<PrefixNamespaceTuple> nsList = (prefixNamespaceTupleList == null)
            ? new ArrayList<>()
            : new ArrayList<>(prefixNamespaceTupleList);

        Node parent = node.getParentNode();

        if (parent == null) {
            return nsList;
        }

        NamedNodeMap parentAttributes = parent.getAttributes();
        if (parentAttributes != null) {
            for (int i = 0; i < parentAttributes.getLength(); i++) {
                Node attr = parentAttributes.item(i);
                if (attr != null && attr.getNodeName() != null && attr.getNodeName().startsWith("xmlns:")) {
                    nsList.add(
                        new PrefixNamespaceTuple(
                            attr.getNodeName().replaceFirst("xmlns:", ""),
                            attr.getNodeValue()));
                }
            }
        }
        return collectAncestorNamespaces(parent, nsList);
    }
    private boolean isNonExclusiveC14n(String algorithm) {
        boolean ret = algorithm.equals(Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS)
                || algorithm.equals(Identifier.CANONICAL_XML_1_0_WITH_COMMENTS);
        return ret;
    }

    private String getCanonicalSignedInfoXml(Document doc) throws CanonicalizationException {

        NodeList signedInfo = XPathSupport.findNodeChildren(this.signatureNode, "SignedInfo");
        if (signedInfo.getLength() == 0) {
            throw new RuntimeException("Could not find SignedInfo element in the message");
        }

        if(isNonExclusiveC14n(this.canonicalizationAlgorithm) && doc == null) {
            throw new IllegalArgumentException("doc: whole xml must be provided for non-exclusive C14n");
        }

        /*
         * Search for ancestor namespaces before canonicalization.
         */
        List<PrefixNamespaceTuple> ancestorNamespaces = this.findAncestorNamespaces(doc, "//*[local-name()='SignedInfo']");
        CanonicalOptions options = new CanonicalOptions();
        options.setAncestorNamespaces(ancestorNamespaces);

        return this.getCanonicalXml(
            Collections.singletonList(this.canonicalizationAlgorithm),
            signedInfo.item(0),
            options);
    }

    /*
     * Search for ancestor namespaces before canonicalization.
     */
    private String getCanonicalReferenceXml(Document doc, XmlReference ref, Node node) throws CanonicalizationException {
        ref.setAncestorNamespaces(findAncestorNamespaces(doc, ref.getXpathExpression()));
        CanonicalOptions options = new CanonicalOptions();
        options.setInclusiveNamespacesPrefixList(ref.getInclusiveNamespacesPrefixList());
        options.setAncestorNamespaces(ref.getAncestorNamespaces());
        return this.getCanonicalXml(ref.getTransforms(), node, options);
    }



    private boolean validateSignatureValue(Document doc) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CanonicalizationException {
        final String localName = "SignedInfo";
        String signedInfoCanonical = getCanonicalSignedInfoXml(doc);
        SignatureAlgorithm signer = this.findSignatureAlgorithm(this.signatureAlgorithm);
        final boolean ret = signer.verify(signedInfoCanonical, this.signingKey, this.signatureValue, null);
        if (!ret) {
            this.errors.add("Invalid signature: The signature value is incorrect: " + this.signatureValue);
        }
        return ret;
    }

    private void calculateSignatureValue(Document doc, char[] passphrase) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CanonicalizationException {
        final char[] finalPassphrase = passphrase == null ? new char[0] : passphrase;
        String signedInfoCanonical = getCanonicalSignedInfoXml(doc);
        SignatureAlgorithm signer = this.findSignatureAlgorithm(this.signatureAlgorithm);
        this.signatureValue = signer.sign(signedInfoCanonical, this.signingKey, finalPassphrase, null);
    }

    private SignatureAlgorithm findSignatureAlgorithm(String name) {
        SignatureAlgorithm alg = Algorithms.getInstance().getSignatureAlgorithms().get(name);
        if (alg != null) {
            return alg;
        }
        throw new RuntimeException("Signature algorithm is not supported: " +  name);
    }

    private CanonicalXml findCanonicalizationAlgorithm(String name) {
        CanonicalXml alg = Algorithms.getInstance().getCanonicalizationAlgorithms().get(name);
        if (alg != null) {
            return alg;
        }
        throw new RuntimeException("Canonicalization algorithm is not supported: " + name);
    }

    private HashAlgorithm findHashAlgorithm(String name) {
        HashAlgorithm alg = Algorithms.getInstance().getHashAlgorithms().get(name);
        if (alg != null) {
            return alg;
        }
        throw new RuntimeException("Hash algorithm is not supported: " + name);
    }

    private boolean validateReferences(Document doc, String xml) throws NoSuchProviderException, NoSuchAlgorithmException, CanonicalizationException {

        for (XmlReference ref : this.references) {
            String uri = ref.getUri().startsWith("#") ? ref.getUri().substring(1) : ref.getUri();
            NodeList elem;
            List<String> elem2= new ArrayList<>();

            if (uri.isEmpty()) {
                elem = select.apply(doc, "//*");
            } else if (uri.contains("'")) {
                // xpath injection
                throw new IllegalStateException("Cannot validate uri with quotes inside it");
            } else {
                String elemXpathExpression = null;
                int numElementsForId = 0;

                for (String idAttr : idAttributes) {

                    String tmpElemXpathExpression = "//*[@*[local-name(.)='" + idAttr + "']='" + uri + "']";
                    try {
                        SAXIdAttributeReader idReader = AssertionFactory.newIdAttributeReader(xml, uri, idAttr);
                        numElementsForId += idReader.getElements().size();
                        if (!idReader.getElements().isEmpty()) {
                            elem2 = idReader.getElements();
                            elemXpathExpression = tmpElemXpathExpression;
                        }
                    } catch (SAXException e) {
                        LOG.error(e.getMessage(), e);
                        errors.add(e.getMessage());
                        return false;
                    }
                }

                if ( numElementsForId > 1) {
                    final String err =
                        "Cannot validate a document which contains multiple elements with the " +
                        "same value for the ID / Id / Id attributes, in order to prevent " +
                        "signature wrapping attack.";
                    LOG.error(err);
                    errors.add(err);
                    return false;
                }

                ref.setXpathExpression(elemXpathExpression);
            }

            if (elem2.isEmpty()) {
                this.errors.add(
                    String.format(
                        "invalid signature: the signature references an element with uri %1$s but could not find such element in the xml",
                        ref.getUri()));
                return false ;
            }

            elem = select.apply(doc, ref.getXpathExpression());

            String canonicalXml = getCanonicalReferenceXml(doc, ref, elem.item(0));
            HashAlgorithm hash = this.findHashAlgorithm(ref.getDigestAlgorithm());
            String digest = hash.hash(canonicalXml, null);

            boolean valid = digest.equals(ref.getDigestValue());

            if (!valid) {
                this.errors.add(
                    "invalid signature: for uri " + ref.getUri() +
                    " calculated digest is " + digest +
                    " but the xml to validate supplies digest " + ref.getDigestValue());

                return false;
            }
        }

        return true;
    }

    private String getKeyInfo(String prefix) {

        String currentPrefix = prefix == null ? "" : prefix;
        if (StringSupport.notNullOrEmpty(currentPrefix)) {
            currentPrefix += ':';
        }
        StringBuilder b = new StringBuilder();
        if (this.keyInfoProvider != null) {
            String tagName = currentPrefix + "KeyInfo";
            XmlSupport.buildStartTag(b, tagName);
            b.append(this.keyInfoProvider.getKeyInfo(this.signingKey));
            XmlSupport.buildEndTag(b, tagName);
        }

        return b.toString();
    }

    /**
     * Generate the Reference nodes (as part of the signature process)
     * @param doc The document
     * @param prefix The prefix
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    private String createReferences(Document doc, String prefix) throws NoSuchProviderException, NoSuchAlgorithmException, CanonicalizationException {

        String effectivePrefix = prefix == null ? "" : prefix;
        if (StringSupport.notNullOrEmpty(effectivePrefix)) {
            effectivePrefix += ':';
        }

        StringBuilder builder = new StringBuilder();

        for (XmlReference ref : this.references) {

            NodeList nodes = select.apply(doc, ref.getXpathExpression());
            if (nodes.getLength() == 0) {
                throw new IllegalStateException(
                    "The following xpath cannot be signed because it was not found: " + ref.getXpathExpression());
            }

            for (int i = 0; i < nodes.getLength(); i++) {

                Element node = (Element)nodes.item(i);

                if (ref.isEmptyUri()) {
                    builder.append('<')
                        .append(effectivePrefix)
                        .append("Reference URI=\"\">");
                } else {
                    String id = this.ensureHasId(node);
                    ref.setUri(id);
                    builder.append('<')
                        .append(effectivePrefix)
                        .append("Reference URI=\"#")
                        .append(id)
                        .append("\">");
                }
                builder.append('<')
                    .append(effectivePrefix)
                    .append("Transforms>");

                for (String t : ref.getTransforms()) {
                    CanonicalXml transform = this.findCanonicalizationAlgorithm(t);
                    builder.append('<')
                        .append(effectivePrefix)
                        .append("Transform Algorithm=\"")
                        .append(transform.getIdentifier())
                        .append("\"/>");
                }

                String canonicalXml = this.getCanonicalReferenceXml(doc, ref, node);

                HashAlgorithm digestAlgorithm = this.findHashAlgorithm(ref.getDigestAlgorithm());
                builder.append("</").append(effectivePrefix).append("Transforms>")
                    .append("<")
                    .append(effectivePrefix)
                    .append("DigestMethod Algorithm=\"")
                    .append(digestAlgorithm.getIdentifier())
                    .append("\"/>")
                    .append("<")
                    .append(effectivePrefix)
                    .append("DigestValue>")
                    .append(digestAlgorithm.hash(canonicalXml, null))
                    .append("</")
                    .append(effectivePrefix)
                    .append("DigestValue>")
                    .append("</")
                    .append(effectivePrefix)
                    .append("Reference>");
            }
        }
        return builder.toString();
    }

    private String getCanonicalXml(final List<String> transforms, final Node node, CanonicalOptions options) throws CanonicalizationException {

        final CanonicalOptions finalOpts = options == null ? new CanonicalOptions() : options;
        if (finalOpts.getDefaultNsForPrefix().isEmpty()) {
            finalOpts.setDefaultNsForPrefix(new DefaultNamespaceContextMap());
        }
        finalOpts.setSignatureNode(this.signatureNode);

        Node copy = node;

        final List<String> finalTransforms = transforms == null ? new ArrayList<>() : transforms;
        for (String t : finalTransforms) {
            CanonicalXml transform = findCanonicalizationAlgorithm(t);
            copy = transform.apply(copy, options);
        }

        return XmlSupport.stringify(copy);
    }

    /*
     * Ensure an element has Id attribute.
     * If not create it with unique value.
     * Work with both normal and wssecurity Id flavour
     */
    private String ensureHasId(Element node) {

        Node attr;
        if ("wssecurity".equals(this.idMode)) {
            attr = XPathSupport.findAttribute(node,
                    "Id", Identifier.WSU_NS_URI);
        } else {
            for (String idVariant : this.idAttributes) {
                attr = XPathSupport.findAttribute(node, idVariant);
                if (attr != null) {
                   return attr.getNodeValue();
                }
            }
        }
        //add the attribute
        String id = "_" + this.id++;

        if ("wssecurity".equals(this.idMode)) {
            node.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:" + Identifier.WSU_NS_PREFIX,
                    Identifier.WSU_NS_URI);
            node.setAttributeNS(Identifier.WSU_NS_URI,
                    Identifier.WSU_NS_PREFIX + ":Id",
                    id);
        } else {
            node.setAttribute("Id", id);
        }

        return id;
    }

    /**
     * Create SignedInfo element
     * @param doc
     * @param prefix
     * @return The SignedInfo String
     */
    private String createSignedInfo(Document doc, String prefix) throws NoSuchProviderException, NoSuchAlgorithmException, CanonicalizationException {
        CanonicalXml transform = this.findCanonicalizationAlgorithm(this.canonicalizationAlgorithm);
        SignatureAlgorithm sigAlg = this.findSignatureAlgorithm(this.signatureAlgorithm);
        String currentPrefix = prefix == null ? "" : prefix;
        if (!currentPrefix.isEmpty()) {
            currentPrefix += ":";
        }

        StringBuilder b = new StringBuilder();

        b.append("<" + currentPrefix + "SignedInfo>");
        b.append("<" + currentPrefix + "CanonicalizationMethod Algorithm=\"" + transform.getIdentifier() + "\"/>");
        b.append("<" + currentPrefix + "SignatureMethod Algorithm=\"" + sigAlg.getIdentifier() + "\"/>");

        b.append(this.createReferences(doc, prefix));
        b.append("</" + currentPrefix + "SignedInfo>");
        return b.toString();
    }

    /*
     * Create the Signature node
     *
     * @param prefix The prefix
     * @return the Signature elmenet
     * @throws IOException
     * @throws SAXException
     */
    private Node createSignature(String prefix) throws IOException, SAXException{
        String xmlnsAttr = "xmlns";

        if (StringSupport.notNullOrEmpty(prefix)) {
            xmlnsAttr += ':' + prefix;
            prefix += ':';
        } else {
            prefix = "";
        }

        String signatureValueXml = "<" + prefix + "SignatureValue>" + this.signatureValue + "</" + prefix + "SignatureValue>";
        // the canonicalization requires to get a valid xml node.
        // we need to wrap the info in a dummy signature since it contains the default namespace.

        String dummySignatureWrapper = "<" + prefix + "Signature " + xmlnsAttr + "=\"http://www.w3.org/2000/09/xmldsig#\">" +
                signatureValueXml +
                "</" + prefix + "Signature>";

        Document doc = XmlSupport.toDocument(dummySignatureWrapper);
        return doc.getDocumentElement().getFirstChild();
    }
}

