package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.Identifier;
import liquer.alchemy.crypto.alg.HashAlgorithm;
import liquer.alchemy.crypto.alg.SignatureAlgorithm;
import liquer.alchemy.crypto.xml.core.Location;
import liquer.alchemy.crypto.xml.core.NodeReader;
import liquer.alchemy.crypto.xml.core.PrefixNamespaceTuple;
import liquer.alchemy.crypto.xml.core.SignatureOptions;
import liquer.alchemy.crypto.xml.core.EOL;
import liquer.alchemy.crypto.xml.saml.DefaultNamespaceContextMap;
import liquer.alchemy.crypto.xml.c14n.CanonicalOptions;
import liquer.alchemy.crypto.xml.c14n.CanonicalXml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.namespace.NamespaceContext;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static liquer.alchemy.crypto.xml.XmlSupport.buildAttribute;
import static liquer.alchemy.alembic.StringSupport.isNullOrEmpty;
import static liquer.alchemy.alembic.StringSupport.notNullOrEmpty;

/**
 * https://www.w3.org/TR/xmldsig-core/
 */
public final class XmlSigner extends NodeReader {

    private static Logger LOG = LogManager.getLogger(XmlSigner.class);
    private static final String XPATH_EXPR_FORMAT_ALG = ".//*[local-name(.)='%1$s']/@Algorithm";
    private static final String XPATH_EXPR_C14N_ALG = String.format(XPATH_EXPR_FORMAT_ALG, "CanonicalizationMethod");
    private static final String XPATH_EXPR_SIG_ALG = String.format(XPATH_EXPR_FORMAT_ALG, "SignatureMethod");
    private static final String XPATH_EXPR_SIGNED_INFO_REF = ".//*[local-name(.)='SignedInfo']/*[local-name(.)='Reference']";
    private static final String XPATH_EXPR_SIGNATURE_VALUE_TEXT = ".//*[local-name(.)='SignatureValue']/text()";
    private static final String XPATH_EXPR_KEY_INFO = ".//*[local-name(.)='KeyInfo']";

    private final String idMode;
    private final XmlSignerOptions options;
    private final List<XmlReference> references;
    private final NamespaceContext namespaceContext;
    private final BiFunction<Node, String, Stream<Node>> select;
    private final EOL eol;
    private final boolean preferSelfClosingTag;

    private int id;
    private String signingKey;
    private KeyInfo keyInfoProvider;
    private String signedXml;
    private String signatureXml;
    private Node signatureNode;
    private String signatureValue;
    private String originalXmlWithIds;
    private List<String> validationErrors;
    private List<Node> keyInfo;
    private Set<String> idAttributes;
    private List<String> implicitTransforms;
    private String signatureAlgorithm;
    private String canonicalizationAlgorithm;

    /**
     *
     */
    public XmlSigner() {
        this(new XmlSignerOptions());
    }


    /**
     * Xml signature implementation
     *
     * @param options Initial configurations
     */
    public XmlSigner(XmlSignerOptions options) {
        this.options = (options == null) ? new XmlSignerOptions() : options;

        this.namespaceContext = this.options.getNamespaceContext();
        this.select = XPathSupport.getSelectStreamClosure(this.namespaceContext);
        this.eol = this.options.getEol();
        this.idMode = this.options.getIdMode();
        this.references = new ArrayList<>();
        this.validationErrors = new ArrayList<>();
        this.preferSelfClosingTag = this.options.isPreferSelfClosingTag();

        this.id = 0;
        this.signingKey = null;
        this.keyInfoProvider = null;
        this.signedXml = "";
        this.signatureXml = "";
        this.signatureNode = null;
        this.signatureValue = "";
        this.originalXmlWithIds = "";
        this.signatureAlgorithm = this.options.getSignatureAlgorithm();
        this.canonicalizationAlgorithm = this.options.getCanonicalizationAlgorithm();
        this.keyInfo = null;
        this.idAttributes = new HashSet<>(Arrays.asList("Id", "ID", "id"));
        if (this.options.getIdAttributes() != null) {
            this.idAttributes.addAll(this.options.getIdAttributes());
        }
        this.implicitTransforms = this.options.getImplicitTransforms();
    }

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

    /**
     * synchronized?
     * @param signatureNode
     */
    public void loadSignature(Node signatureNode) {
        if (signatureNode == null) {
            throw new IllegalArgumentException("Argument 'signatureNode' cannot be null");
        }
        this.signatureNode = signatureNode;
        this.signatureXml = XmlSupport.stringify(signatureNode);
        this.canonicalizationAlgorithm =
            select.apply(signatureNode, XPATH_EXPR_C14N_ALG)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find Canonicalization/@Algorithm element"))
                .getNodeValue();

        this.select.apply(signatureNode, XPATH_EXPR_SIG_ALG)
            .findFirst()
            .ifPresent(n-> this.signatureAlgorithm  = n.getNodeValue());

        this.select.apply(signatureNode, XPATH_EXPR_SIGNED_INFO_REF)
            .forEach(this::loadReference);

        if (this.references.isEmpty()) {
            throw new RuntimeException("Could not find any Reference elements");
        }

        this.signatureValue = select.apply(signatureNode, XPATH_EXPR_SIGNATURE_VALUE_TEXT)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Could not find any SignatureValue"))
            .getTextContent();

        this.keyInfo = select.apply(signatureNode, XPATH_EXPR_KEY_INFO)
            .collect(Collectors.toList());

        if (this.keyInfoProvider != null) {
            this.signingKey = this.keyInfoProvider.getKey(this.keyInfo);
        }
    }

    /**
     * synchronized?
     * @param xml
     * @return
     */
    public ValidationResult validateSignature(String xml) {

        this.validationErrors = new ArrayList<>();
        this.signedXml = xml;

        if (this.keyInfoProvider == null) {
            throw new RuntimeException("Cannot validate signature: no key info resolver was provided");
        }

        this.signingKey = this.keyInfoProvider.getKey(this.keyInfo);
        if (this.signingKey == null) {
            throw new RuntimeException("Could not resolve key info: " + this.keyInfo);
        }

        final Document doc = XmlSupport.toDocument(xml);

        final boolean validSignature =
                validateReferences(doc) && validateSignatureValue(doc);

        final List<String> validationErrors = Collections.unmodifiableList(this.validationErrors);

        return new ValidationResult() {

            @Override
            public boolean isValidSignature() { return validSignature; }

            @Override
            public boolean isValidToken() { return validationErrors.isEmpty(); }

            @Override
            public List<String> getValidationErrors() { return validationErrors; }
        };
    }

    public void addReference(String xpathExpression) {
        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(xpathExpression);
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
     * synchronized?
     * @param xml
     */
    public void computeSignature(String xml) {
        computeSignature(xml, null);
    }

    /**
     * Compute the signature of the given xml (usign the already defined settings)
     *
     * synchronized?
     * @param xml The xml String
     * @param options The signature options
     */
    public void computeSignature(String xml, SignatureOptions options) {

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
        if (notNullOrEmpty(prefix)) {
            xmlnsAttr += ":" + prefix;
            currentPrefix = prefix + ":";
        } else {
            currentPrefix = "";
        }

        for(Map.Entry<String, String> entry : attrs.entrySet()) {
            String name = entry.getKey();
            if ("xmlns".equals(name) && !xmlnsAttr.equals(name)) {
                signatureAttrs.add(buildAttribute(name, entry.getValue()).toString());
            }
        };

        // add the xml namespace attribute
        signatureAttrs.add(buildAttribute(xmlnsAttr, Identifier.DEFAULT_NS_URI).toString());

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

        this.originalXmlWithIds = XmlSupport.stringify(doc); // xml; //

        final StringBuilder existingPrefixesBuilder = new StringBuilder();

        existingPrefixes.forEach((key, value) -> {
            buildAttribute(existingPrefixesBuilder, "xmlns:" + key, value);
        });

        // A trick to remove the namespaces that already exist in the xml
        // This only works if the prefix and namespace match with those in the xml
        final Document wrappedDoc = XmlSupport.toWrappedDocument("Dummy", existingPrefixesBuilder.toString(), signatureXml.toString());
        final Node firstChild = wrappedDoc.getDocumentElement().getFirstChild().cloneNode(true);
        final Node signatureDoc = doc.adoptNode(firstChild);
        final Node referenceNode = this.select.apply(doc, location.getReference())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "The following xpath cannot be used because it was not found: " +
                                location.getReference()));

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
        this.calculateSignatureValue(doc);

        final NodeList signedInfoNodes = XPathSupport.findNodeChildren(this.signatureNode, "SignedInfo");
        if (signedInfoNodes.getLength() == 0) {
            throw new RuntimeException("Could not find SignedInfo element in the message");
        }

        final Node signedInfoNode = signedInfoNodes.item(0);
        signatureDoc.insertBefore(doc.adoptNode(this.createSignature(prefix)), signedInfoNode.getNextSibling());

        this.signatureXml = XmlSupport.stringify(signatureDoc);
        this.signedXml = XmlSupport.stringify(doc);
    }

    /*
     * Extract ancestor namespaces in order to import it to root of document subset
     * which is being canonicalized for non-exclusive c14n.
     *
     * @param doc - Usually a product from `new DOMParser().parseFromString()`
     * @param docSubsetXpath - xpath query to get document subset being canonicalized
     * @return The PrefixNamespaceTuple list, i.e. [{prefix: "saml", namespaceURI: "urn:oasis:names:tc:SAML:2.0:assertion"}]
     */
    private List<PrefixNamespaceTuple> findAncestorNamespaces(Document doc, String docSubsetXpath){
        List<Node> docSubset = select.apply(doc, docSubsetXpath)
                .collect(Collectors.toList());

        if (docSubset.isEmpty()) {
            return new ArrayList<>();
        }

        // Remove duplicate on ancestor namespace
        List<PrefixNamespaceTuple> ancestorNs = collectAncestorNamespaces(docSubset.get(0), null);
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
        NamedNodeMap subsetAttributes = docSubset.get(0).getAttributes();
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

    private List<PrefixNamespaceTuple> collectAncestorNamespaces(Node node, List<PrefixNamespaceTuple> prefixNamspaceTupleList){

        List<PrefixNamespaceTuple> nsList = prefixNamspaceTupleList;

        if(nsList == null){
            nsList = new ArrayList<>();
        }

        Node parent = node.getParentNode();

        if(parent == null){
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

    private String getCanonicalSignedInfoXml(Document doc) {

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
    private String getCanonicalReferenceXml(Document doc, XmlReference ref, Node node) {
        ref.setAncestorNamespaces(findAncestorNamespaces(doc, ref.getXpathExpression()));
        CanonicalOptions options = new CanonicalOptions();
        options.setInclusiveNamespacesPrefixList(ref.getInclusiveNamespacesPrefixList());
        options.setAncestorNamespaces(ref.getAncestorNamespaces());
        return this.getCanonicalXml(ref.getTransforms(), node, options);
    }

    private boolean validateSignatureValue(Document doc) {
        final String localName = "SignedInfo";
        String signedInfoCanonical = getCanonicalSignedInfoXml(doc);
        SignatureAlgorithm signer = this.findSignatureAlgorithm(this.signatureAlgorithm);
        final boolean ret = signer.verify(signedInfoCanonical, this.signingKey, this.signatureValue, null);
        if (!ret) {
            this.validationErrors.add("Invalid signature: The signature value is incorrect: " + this.signatureValue);
        }
        return ret;
    }

    private void calculateSignatureValue(Document doc) {
        String signedInfoCanonical = getCanonicalSignedInfoXml(doc);
        SignatureAlgorithm signer = this.findSignatureAlgorithm(this.signatureAlgorithm);
        this.signatureValue = signer.sign(signedInfoCanonical, this.signingKey, null);
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

    private boolean validateReferences(Document doc) {

        for (XmlReference ref : this.references) {
            String uri = ref.getUri().startsWith("#") ? ref.getUri().substring(1) : ref.getUri();
            List<Node> elem = new ArrayList<>();


            if (uri.isEmpty()) {
                elem = select.apply(doc, "//*").collect(Collectors.toList());
            } else if (uri.contains("'")) {
                // xpath injection
                throw new RuntimeException("Cannot validate uri with quotes inside it");
            } else {

                String elemXpathExpression = null;
                int numElementsForId = 0;
                for (String idAttr : this.idAttributes) {

                    String tmpElemXpathExpression = "//*[@*[local-name(.)='" + idAttr + "']='" + uri + "']";
                    List<Node> tmpElem = select.apply(doc, tmpElemXpathExpression).collect(Collectors.toList());
                    numElementsForId += tmpElem.size();
                    if (!tmpElem.isEmpty()) {
                        elem = tmpElem;
                        elemXpathExpression = tmpElemXpathExpression;
                    }
                }
                if ( numElementsForId > 1) {
                    throw new RuntimeException(
                            "Cannot validate a document which contains multiple elements with the " +
                            "same value for the ID / Id / Id attributes, in order to prevent " +
                            "signature wrapping attack.");
                }

                ref.setXpathExpression(elemXpathExpression);
            }

            if (elem.isEmpty()) {
                this.validationErrors.add(
                    String.format(
                        "invalid signature: the signature refernces an element with uri %1$s but could not find such element in the xml",
                        ref.getUri()));
                return false ;
            }

            String canonicalXml = this.getCanonicalReferenceXml(doc, ref, elem.get(0));

            HashAlgorithm hash = this.findHashAlgorithm(ref.getDigestAlgorithm());

            String digest = hash.hash(canonicalXml, null);

            boolean valid = validateDigestValue(digest, ref.getDigestValue());
            if (!valid) {
                this.validationErrors.add(
                        "invalid signature: for uri " + ref.getUri() +
                        " calculated digest is " + digest +
                        " but the xml to validate supplies digest " + ref.getDigestValue());

                return false;
            }
        }

        return true;
    }

    private boolean validateDigestValue(String digest, String expectedDigest) {
        return digest.equals(expectedDigest);
    }

    /*
     * Load the reference xml node to a model
     */
    private void loadReference(Node ref) {
        NodeList nodes = XPathSupport.findNodeChildren(ref, "DigestMethod");
        if (nodes.getLength() == 0) {
            throw new RuntimeException(
                   "Could not find DigestMethod node in reference " + XmlSupport.stringify(ref));
        }
        Node digestAlgorithmNode = nodes.item(0);
        Node attr = XPathSupport.findAttribute(digestAlgorithmNode, "Algorithm", null);
        if (attr == null) {
            throw new RuntimeException(
                    "Could not find Algorithm attribute in node " + XmlSupport.stringify(digestAlgorithmNode));
        }
        String digestAlgorithm = attr.getNodeValue();

        NodeList digestValueNodes = XPathSupport.findNodeChildren(ref, "DigestValue");
        if (digestValueNodes.getLength() == 0) {
            throw new RuntimeException(
                    "Could not find DigestValue node in reference " + XmlSupport.stringify(ref));
        }
        Node firstDigestValueNode = digestValueNodes.item(0);
        if (!firstDigestValueNode.hasChildNodes()
                || isNullOrEmpty(firstDigestValueNode.getFirstChild().getTextContent())) {
            throw new RuntimeException(
                    "Could not find the value of DigestValue in " + XmlSupport.stringify(firstDigestValueNode));
        }

        String digestValue =  firstDigestValueNode.getFirstChild().getTextContent();

        List<String> transforms = new ArrayList<>();
        List<String> inclusiveNamespacesPrefixList = new ArrayList<>();

        NodeList transformsNodes = XPathSupport.findNodeChildren(ref, "Transforms");

        if (transformsNodes.getLength() > 0) {
            Node transformsNode = transformsNodes.item(0);
            NodeList transformsAll = XPathSupport.findNodeChildren(transformsNode, "Transform");
            Node trans = null;
            for (int i = 0; i < transformsAll.getLength(); i++) {
                trans = transformsAll.item(i);
                Node algorithmAttr = XPathSupport.findAttribute(trans, "Algorithm", null);
                if (algorithmAttr != null) {
                    transforms.add(algorithmAttr.getNodeValue());
                }

            }

            NodeList inclusiveNamespaces = XPathSupport.findNodeChildren(trans, "InclusiveNamespaces");
            if (inclusiveNamespaces.getLength() > 0) {
                //Should really only be one prefix list, but maybe there's some circumstances where more than one to lets handle it
                for (int i = 0; i < inclusiveNamespaces.getLength(); i++) {
                    Node prefixListAttr = inclusiveNamespaces.item(i).getAttributes().getNamedItem("PrefixList");
                    inclusiveNamespacesPrefixList.add(prefixListAttr.getNodeValue());
                }
            }
        }

        boolean hasImplicitTransforms = (this.implicitTransforms != null && !this.implicitTransforms.isEmpty());
        if(hasImplicitTransforms){
            this.implicitTransforms.addAll(transforms);
        }

        /*
         * DigestMethods take an octet stream rather than a node set.
         * If the output of the last transform is a node set,
         * we need to canonicalize the node set to an octet stream
         * using non-exclusive canonicalization.
         * If there are no transforms,
         * we need to canonicalize because URI de-referencing for a same-document reference
         * will return a node-set.
         * See:
         * https://www.w3.org/TR/xmldsig-core1/#sec-DigestMethod
         * https://www.w3.org/TR/xmldsig-core1/#sec-ReferenceProcessingModel
         * https://www.w3.org/TR/xmldsig-core1/#sec-Same-Document
         */
        if (transforms.isEmpty() || Identifier.ENVELOPED_SIGNATURE.equals(transforms.get(transforms.size()- 1))) {
            transforms.add(Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS);
        }

        final String uriNodeValue = readNodeValue(XPathSupport.findAttribute(ref, "URI"));

        this.addReference(
            null,
            transforms,
            digestAlgorithm,
            uriNodeValue,
            digestValue,
            inclusiveNamespacesPrefixList,
            false);
    }

    private String getKeyInfo(String prefix) {

        String currentPrefix = prefix == null ? "" : prefix;
        if (notNullOrEmpty(currentPrefix)) {
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
     */
    private String createReferences(Document doc, String prefix) {

        String effectivePrefix = prefix == null ? "" : prefix;
        if (notNullOrEmpty(effectivePrefix)) {
            effectivePrefix += ':';
        }

        StringBuilder builder = new StringBuilder();

        for (XmlReference ref : this.references) {

            List<Node> nodes = select.apply(doc, ref.getXpathExpression()).collect(Collectors.toList());
            if (nodes.isEmpty()) {
                throw new RuntimeException(
                        "The following xpath cannot be signed because it was not found: " + ref.getXpathExpression());
            }

            for (Node n : nodes) {

                Element node = (Element)n;

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

    private String getCanonicalXml(final List<String> transforms, final Node node, CanonicalOptions options) {

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

        return XmlSupport.stringify(copy, preferSelfClosingTag, this.eol);
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
    private String createSignedInfo(Document doc, String prefix) {
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
     */
    private Node createSignature(String prefix) {
        String xmlnsAttr = "xmlns";

        if (notNullOrEmpty(prefix)) {
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

