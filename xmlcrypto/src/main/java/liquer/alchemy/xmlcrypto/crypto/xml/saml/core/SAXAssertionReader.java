package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.*;
import liquer.alchemy.xmlcrypto.crypto.xml.core.KeyInfoImpl;
import liquer.alchemy.xmlcrypto.crypto.xml.core.X509DataImpl;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.*;
import liquer.alchemy.xmlcrypto.functional.Func0;
import liquer.alchemy.xmlcrypto.functional.Func1;
import liquer.alchemy.xmlcrypto.support.StringSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.DatatypeConverter;
import java.util.*;


/**
 * Reads Assertion with SAX
 */
final class SAXAssertionReader extends DefaultHandler implements Assertion {

    private static final Logger LOG = LogManager.getLogger(SAXAssertionReader.class);

    private enum AssertionElement {
        UNDEFINED,
        Assertion,
        Issuer,
        Subject, SubjectConfirmation,
        Conditions, AudienceRestriction, Audience,
        AttributeStatement, Attribute, AttributeValue,

        // Signature
        Signature,
        CanonicalizationMethod,
        SignatureMethod,
        SignedInfo,
        Reference,
        DigestMethod,
        DigestValue,
        Transforms,
        Transform,
        InclusiveNamespaces,
        SignatureValue,
        KeyInfo,
        X509Data,
        X509Certificate,
        X509IssuerSerial,
        X509IssuerName,
        X509SerialNumber
    }

    private static class LocalNames {
        private static final String ASSERTION = "Assertion";
        private static final String ISSUER = "Issuer";
        private static final String SUBJECT = "Subject";
        private static final String SUBJECT_CONFIRMATION = "SubjectConfirmation";
        private static final String CONDITIONS = "Conditions";
        private static final String AUDIENCE_RESTRICTION = "AudienceRestriction";
        private static final String AUDIENCE = "Audience";
        private static final String ATTRIBUTE_STATEMENT = "AttributeStatement";
        private static final String ATTRIBUTE = "Attribute";
        private static final String ATTRIBUTE_VALUE = "AttributeValue";
    }

    private static class QNames {
        private static final String NS_PREFIX = "saml:";
        private static final String ASSERTION = NS_PREFIX + LocalNames.ASSERTION;
        private static final String ISSUER = NS_PREFIX + LocalNames.ISSUER;
        private static final String SUBJECT = NS_PREFIX + LocalNames.SUBJECT;
        private static final String SUBJECT_CONFIRMATION = NS_PREFIX + LocalNames.SUBJECT_CONFIRMATION;
        private static final String CONDITIONS = NS_PREFIX + LocalNames.CONDITIONS;
        private static final String AUDIENCE_RESTRICTION = NS_PREFIX + LocalNames.AUDIENCE_RESTRICTION;
        private static final String AUDIENCE = NS_PREFIX + LocalNames.AUDIENCE;
        private static final String ATTRIBUTE_STATEMENT = NS_PREFIX + LocalNames.ATTRIBUTE_STATEMENT;
        private static final String ATTRIBUTE = NS_PREFIX + LocalNames.ATTRIBUTE;
        private static final String ATTRIBUTE_VALUE = NS_PREFIX + LocalNames.ATTRIBUTE_VALUE;

        private static final String ISSUE_INSTANT = "IssueInstant";
        private static final String VERSION = "Version";
        private static final String ID = "ID";
        private static final String METHOD = "Method";
        private static final String NOT_BEFORE = "NotBefore";
        private static final String NOT_ON_OR_AFTER = "NotOnOrAfter";
        private static final String NAME = "Name";

        // Signature
        private static final String SIGNATURE = "Signature";
        private static final String CANONICALIZATION_METHOD = "CanonicalizationMethod";
        private static final String SIGNATURE_METHOD = "SignatureMethod";
        private static final String SIGNED_INFO = "SignedInfo";
        private static final String REFERENCE = "Reference";
        private static final String DIGEST_METHOD = "DigestMethod";
        private static final String DIGEST_VALUE = "DigestValue";
        private static final String TRANSFORMS = "Transforms";
        private static final String TRANSFORM = "Transform";
        private static final String INCLUSIVE_NAMESPACES = "InclusiveNamespaces";

        private static final String X509_DATA = "X509Data";
        private static final String X509_CERTIFICATE = "X509Certificate";
        private static final String X509_ISSUER_SERIAL = "X509IssuerSerial";
        private static final String X509_ISSUER_NAME = "X509IssuerName";
        private static final String X509_SERIAL_NUMBER = "X509SerialNumber";

        private static final String SIGNATURE_VALUE = "SignatureValue";
        private static final String KEY_INFO = "KeyInfo";
        private static final String ALGORITHM = "Algorithm";
        private static final String URI = "URI";
        private static final String PREFIX_LIST = "PrefixList";
    }

    private static void renderEndElement(final StringBuilder b, String uri, String localName, String qName) throws SAXException {
        String elementName = localName; // element name
        if ("".equals(elementName)) {
            elementName = qName; // not namespaceAware
        }
        b.append("</" + elementName + '>');
    }

    private static void renderStartElement(final StringBuilder b, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elementName = localName; // element name
        if ("".equals(elementName)) {
            elementName = qName; // not namespaceAware
        }
        b.append('<' + elementName);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String aName = attributes.getLocalName(i); // Attr name
                if ("".equals(aName)) {
                    aName = attributes.getQName(i);
                }
                b.append(' ');
                b.append(aName + "=\""+attributes.getValue(i) + "\"");
            }
        }
        b.append('>');
    }

    static class SAXSignatureReader extends DefaultHandler implements Signature {

        private final String signatureXml;
        private final List<String> implicitTransforms;
        private final List<String> transforms;
        private final List<String> inclusiveNamespacesPrefixList;
        private final List<XmlReference> references;

        private String signatureFragment;
        private String x509Certificate;
        private String x509IssuerName;
        private String x509SerialNumber;
        private String digestValue;
        private String signatureValue;
        private String canonicalizationAlgorithm;
        private String signatureAlgorithm;
        private String digestAlgorithm;
        private String referenceURI;
        private XmlReference assertionReference;
        private KeyInfo keyInfo;

        private boolean hasX509IssuerSerial;

        private StringBuilder signatureFragmentBuilder;
        private StringBuilder digestValueBuilder;
        private StringBuilder signatureValueBuilder;
        private StringBuilder x509CertificateBuilder;
        private StringBuilder x509IssuerNameBuilder;
        private StringBuilder x509SerialNumberBuilder;
        private boolean renderMode;

        // trick sonar and avoid if...else...
        private final Func0<Integer> currentDepthClosure;
        private final Func0<AssertionElement> currentElementClosure;
        private final Func0<AssertionElement> popElementClosure;
        private final Func1<AssertionElement, AssertionElement> pushElementClosure;
        private final Func1<AssertionElement, Boolean> currentHasAncestorClosure;

        private SAXSignatureReader(final SAXAssertionReader assertionReader, XmlSignerOptions options) {
            this.signatureXml = null;
            this.implicitTransforms = options.getImplicitTransforms();
            this.transforms = new ArrayList<>();
            this.inclusiveNamespacesPrefixList = new ArrayList<>();
            this.references = new ArrayList<>();

            this.currentDepthClosure = () -> assertionReader.currentDepth();
            this.currentElementClosure = () -> assertionReader.currentElement();
            this.popElementClosure = () -> assertionReader.popElement();
            this.pushElementClosure = (item) -> assertionReader.pushElement(item);
            this.currentHasAncestorClosure = (ancestor) -> assertionReader.hasAncestor(ancestor);
        }

        SAXSignatureReader(String xml, XmlSignerOptions options) {
            this.signatureXml = xml;
            this.implicitTransforms = options.getImplicitTransforms();
            this.transforms = new ArrayList<>();
            this.inclusiveNamespacesPrefixList = new ArrayList<>();
            this.references = new ArrayList<>();

            final Stack<AssertionElement> elementStack = new Stack<>();
            this.currentDepthClosure = () -> elementStack.size();
            this.currentElementClosure = () -> elementStack.peek();
            this.popElementClosure = () -> elementStack.pop();
            this.pushElementClosure = (item) -> elementStack.push(item);
            this.currentHasAncestorClosure = (ancestor) -> {
                final int pos = elementStack.indexOf(ancestor);
                return (pos != -1 && pos < (elementStack.size()-1));
            };
        }

        @Override public String toString() {
            return signatureXml == null
                ? this.signatureFragment
                : this.signatureXml;
        }

        @Override
        public String getCanonicalizationAlgorithm() { return this.canonicalizationAlgorithm; }

        @Override
        public String getSignatureAlgorithm() { return this.signatureAlgorithm; }

        @Override
        public String getSignatureValue() { return this.signatureValue; }

        @Override
        public List<String> getTransforms() { return Collections.unmodifiableList(this.transforms); }

        @Override
        public List<XmlReference> getReferences() { return Collections.unmodifiableList(this.references); }

        @Override
        public XmlReference getAssertionReference() {
            return this.assertionReference;
        }

        @Override
        public KeyInfo getKeyInfo() { return this.keyInfo; }

        private int currentDepth() {
            return currentDepthClosure.apply();
        }

        private AssertionElement currentElement() {
            return currentElementClosure.apply();
        }

        private AssertionElement pushElement(AssertionElement item) {
            return pushElementClosure.apply(item);
        }

        private AssertionElement popElement() {
            return popElementClosure.apply();
        }

        private boolean currentHasAncestor(AssertionElement ancestor) {
            return currentHasAncestorClosure.apply(ancestor);
        }

        @Override
        public void startDocument() {
            this.signatureFragmentBuilder = new StringBuilder();
            this.digestValueBuilder = new StringBuilder();
            this.signatureValueBuilder = new StringBuilder();
            this.x509CertificateBuilder = new StringBuilder();
            this.x509IssuerNameBuilder = new StringBuilder();
            this.x509SerialNumberBuilder = new StringBuilder();
        }

        @Override
        public void endDocument() {
            final List<String> validationErrors = endAndValidateDocument(null);
            if (!validationErrors.isEmpty()) {
                throw new IllegalStateException("Illegal state: " + String.join(" ", validationErrors));
            }
        }

        private List<String> endAndValidateDocument(String assertionId) {

            final List<String> errors = new ArrayList<>();

            this.signatureFragment = signatureFragmentBuilder.toString();
            this.digestValue = digestValueBuilder.toString();
            this.signatureValue = signatureValueBuilder.toString();
            this.x509Certificate = x509CertificateBuilder.toString();
            this.x509IssuerName = x509IssuerNameBuilder.toString();
            this.x509SerialNumber = x509SerialNumberBuilder.toString();

            final boolean hasCanonicalizationAlgorithm = StringSupport.notNullEmptyOrBlank(this.canonicalizationAlgorithm);
            final boolean hasSignatureAlgorithm = StringSupport.notNullEmptyOrBlank(this.signatureAlgorithm);
            final boolean hasDigestAlgorithm = StringSupport.notNullEmptyOrBlank(this.digestAlgorithm);
            final boolean hasDigestValue = StringSupport.notNullEmptyOrBlank(this.digestValue);
            final boolean hasSignatureValue = StringSupport.notNullEmptyOrBlank(this.signatureValue);

            if (!hasCanonicalizationAlgorithm) {
                errors.add(
                    "Cannot find Algorithm attribute in CanonicalizationMethod Element.");
            }
            if (!hasSignatureAlgorithm) {
                errors.add(
                    "Cannot find Algorithm attribute in SignatureMethod Element.");
            }
            if (!hasSignatureValue) {
                errors.add(
                    "Cannot find the value of the SignatureValue Element.");
            }
            if (!hasDigestAlgorithm) {
                errors.add(
                    "Cannot find Algorithm attribute in DigestMethod Element.");
            }
            if (!hasDigestValue) {
                errors.add(
                    "Cannot not find the value of the DigestValue Element.");
            }

            if (references.isEmpty()) {
                errors.add("Cannot find any Reference elements");
            } else {
                if (assertionId != null) {

                    // enveloped Signature
                    final boolean hasX509Certificate = StringSupport.notNullEmptyOrBlank(this.x509Certificate);
                    final boolean hasX509IssuerName = StringSupport.notNullEmptyOrBlank(this.x509IssuerName);
                    final boolean hasX509SerialNumber = StringSupport.notNullEmptyOrBlank(this.x509SerialNumber);

                    int refIdCount = 0;
                    for (XmlReference ref : references) {
                        String uri = ref.getUri().startsWith("#") ? ref.getUri().substring(1) : ref.getUri();
                        if (assertionId.equals(uri)) {
                            assertionReference = ref;
                            refIdCount++;
                        }
                    }
                    if (refIdCount > 1) {
                        errors.add(
                            "Cannot validate a document which contains multiple references with the " +
                                "same value for the Assertion ID attribute");
                    }
                    if (!hasX509Certificate) {
                        errors.add("Cannot find any X509Certificate value");
                    }
                    if (!hasX509IssuerName) {
                        errors.add("Cannot find any X509IssuerName value");
                    }
                    if (!hasX509SerialNumber) {
                        errors.add("Cannot find any X509SerialNumber value");
                    }

                }
            }

            if (errors.isEmpty()) {
                boolean hasImplicitTransforms = (this.implicitTransforms != null && !this.implicitTransforms.isEmpty());
                if (hasImplicitTransforms) {
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
                if (transforms.isEmpty() || Identifier.ENVELOPED_SIGNATURE.equals(transforms.get(transforms.size() - 1))) {
                    transforms.add(Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS);
                }

                final X509Data x509data = new X509DataImpl(x509IssuerName, x509SerialNumber, x509Certificate);
                this.keyInfo = new KeyInfoImpl(x509data);
            }

            return errors;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {

                case QNames.SIGNATURE:
                    this.pushElement(AssertionElement.Signature);
                    this.renderMode = true;
                    break;
                case QNames.CANONICALIZATION_METHOD:
                    this.pushElement(AssertionElement.CanonicalizationMethod);
                    if (this.currentHasAncestor(AssertionElement.SignedInfo)) {
                        this.canonicalizationAlgorithm = attributes.getValue(QNames.ALGORITHM);
                    }
                    break;
                case QNames.SIGNATURE_METHOD:
                    this.pushElement(AssertionElement.SignatureMethod);
                    if (this.currentHasAncestor(AssertionElement.SignedInfo)) {
                        this.signatureAlgorithm = attributes.getValue(QNames.ALGORITHM);
                     }
                    break;
                case QNames.SIGNED_INFO:
                    this.pushElement(AssertionElement.SignedInfo);
                    break;
                case QNames.REFERENCE:
                    this.pushElement(AssertionElement.Reference);
                    if (this.currentHasAncestor(AssertionElement.SignedInfo)) {
                        this.referenceURI = attributes.getValue(QNames.URI);
                    }
                    break;
                case QNames.DIGEST_METHOD:
                    this.pushElement(AssertionElement.DigestMethod);
                    if (this.currentHasAncestor(AssertionElement.Reference)) {
                        this.digestAlgorithm = attributes.getValue(QNames.ALGORITHM);
                    }
                    break;
                case QNames.DIGEST_VALUE:
                    this.pushElement(AssertionElement.DigestValue);
                    break;
                case QNames.SIGNATURE_VALUE:
                    this.pushElement(AssertionElement.SignatureValue);
                    break;
                case QNames.TRANSFORMS:
                    this.pushElement(AssertionElement.Transforms);
                    break;
                case QNames.TRANSFORM:
                    this.pushElement(AssertionElement.Transform);
                    if (this.currentHasAncestor(AssertionElement.Transforms)) {
                        this.transforms.add(attributes.getValue(QNames.ALGORITHM));
                    }
                    break;
                case QNames.INCLUSIVE_NAMESPACES:
                    this.pushElement(AssertionElement.InclusiveNamespaces);
                    if (this.currentHasAncestor(AssertionElement.Transform)) {
                        this.inclusiveNamespacesPrefixList.add(attributes.getValue(QNames.PREFIX_LIST));
                    }
                    break;
                case QNames.KEY_INFO:
                    this.pushElement(AssertionElement.KeyInfo);
                    break;
                case QNames.X509_DATA:
                    this.pushElement(AssertionElement.X509Data);
                    break;
                case QNames.X509_CERTIFICATE:
                    this.pushElement(AssertionElement.X509Certificate);
                    break;
                case QNames.X509_ISSUER_SERIAL:
                    this.pushElement(AssertionElement.X509IssuerSerial);
                    hasX509IssuerSerial = true;
                    break;
                case QNames.X509_ISSUER_NAME:
                    this.pushElement(AssertionElement.X509IssuerName);
                    break;
                case QNames.X509_SERIAL_NUMBER:
                    this.pushElement(AssertionElement.X509SerialNumber);
                    break;
                default:
                    this.pushElement(AssertionElement.UNDEFINED);
                    break;
            }

            if (this.renderMode) {
                renderStartElement(this.signatureFragmentBuilder, uri, localName, qName, attributes);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case QNames.REFERENCE:
                    final XmlReference xmlReference = new XmlReference();
                    xmlReference.setXpathExpression(null);
                    xmlReference.setTransforms(this.transforms);
                    xmlReference.setDigestAlgorithm(this.digestAlgorithm);
                    xmlReference.setUri(this.referenceURI);
                    xmlReference.setDigestValue(this.digestValueBuilder.toString());
                    xmlReference.setInclusiveNamespacesPrefixList(this.inclusiveNamespacesPrefixList);
                    xmlReference.setEmptyUri(false);
                    this.references.add(xmlReference);
                    break;
                case QNames.SIGNATURE:
                    renderEndElement(this.signatureFragmentBuilder, uri, localName, qName);
                    this.renderMode = false;
                    break;
                default:
                    break;
            }
            if (this.renderMode) {
                renderEndElement(this.signatureFragmentBuilder, uri, localName, qName);
            }

            this.popElement();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            switch (this.currentElement()) {
                case Signature:
                    if (this.renderMode) {
                        this.signatureFragmentBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                case SignatureValue:
                    this.signatureValueBuilder.append(String.valueOf(ch, start, length));
                    break;
                case DigestValue:
                    if (this.currentHasAncestor(AssertionElement.Reference)) {
                        this.digestValueBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                case X509Certificate:
                    if (this.currentHasAncestor(AssertionElement.KeyInfo)) {
                        this.x509CertificateBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                case X509IssuerName:
                    if (this.currentHasAncestor(AssertionElement.X509IssuerSerial)) {
                        this.x509IssuerNameBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                case X509SerialNumber:
                    if (this.currentHasAncestor(AssertionElement.X509IssuerSerial)) {
                        this.x509SerialNumberBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                default:
                    break;
            }
            if (this.renderMode) {
                this.signatureFragmentBuilder.append(String.valueOf(ch, start, length));
            }
        }
    }

    private final String signedXml;
    private final XmlSigner xmlSigner;
    private final List<AudienceRestriction> audienceRestrictions;
    private final List<AttributeStatement> attributeStatements;
    private final List<Subject> subjects;

    private String version;
    private String id;
    private GregorianCalendar issueInstant;
    private GregorianCalendar notBefore;
    private GregorianCalendar notOnOrAfter;
    private Conditions conditions;
    private StringBuilder issuerBuilder;

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public GregorianCalendar getIssueInstant() {
        return issueInstant;
    }

    @Override
    public String getIssuer() {
        return issuerBuilder.toString();
    }

    @Override
    public List<Subject> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public List<AttributeStatement> getAttributeStatements() {
        return Collections.unmodifiableList(attributeStatements);
    }

    @Override
    public XmlSigner getXmlSigner() {
        return xmlSigner;
    }

    @Override
    public ValidationResult validateSignature() {
        return xmlSigner.validateAssertion(this);
    }

    @Override
    public String toString() { return signedXml;  }

    private final SAXSignatureReader signature;

    private final Stack<AssertionElement> elementStack;

    SAXAssertionReader(String xml, XmlSignerOptions options) {
        signedXml = xml;
        elementStack = new Stack<>();
        issuerBuilder = new StringBuilder();
        issuerBuilder = new StringBuilder();
        subjects = new ArrayList<>();
        audienceRestrictions = new ArrayList<>();
        attributeStatements = new ArrayList<>();
        signature = new SAXSignatureReader(this, options);
        signature.startDocument();
        xmlSigner = new XmlSigner(options);
    }

    private int currentDepth() { return elementStack.size(); }

    private AssertionElement currentElement() {
        return elementStack.peek();
    }

    private AssertionElement pushElement(AssertionElement item) {
        return elementStack.push(item);
    }

    private AssertionElement popElement() {
        return elementStack.pop();
    }

    private boolean hasAncestor(AssertionElement parent) {
        int pos = elementStack.indexOf(parent);
        return (pos != -1 && pos < (elementStack.size()-1));
    }

    @Override
    public void endDocument() {
        final List<String> validationErrors = new ArrayList<>();
        if (StringSupport.isNullEmptyOrBlank(this.id)) {
            validationErrors.add(
                "ID Attribute cannot be null, empty or blank.");
        }
        validationErrors.addAll(this.signature.endAndValidateDocument(this.id));
        if (!validationErrors.isEmpty()) {
            throw new IllegalStateException("Illegal state: " + String.join(" ", validationErrors));
        }
        if (this.getSignature() != null) {
            this.xmlSigner.loadSignature(this.getSignature());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        switch (qName) {
            case QNames.ASSERTION:
                elementStack.push(AssertionElement.Assertion);
                if (currentDepth() != 1) {
                    throw new SAXException("Illegal position of " + qName + " Element at depth " + currentDepth());
                }
                startAssertion(uri, localName, qName, attributes);
                break;
            case QNames.ISSUER:
                elementStack.push(AssertionElement.Issuer);
                if (currentDepth() != 2) {
                    throw new SAXException("Illegal position of " + qName + " Element at depth " + currentDepth());
                }
                break;
            case QNames.SUBJECT_CONFIRMATION:
                elementStack.push(AssertionElement.SubjectConfirmation);
                startSubjectConfirmation(uri, localName, qName, attributes);
                break;
            case QNames.CONDITIONS:
                elementStack.push(AssertionElement.Conditions);
                if (currentDepth() != 2) {
                    throw new SAXException("Illegal position of " + qName + " Element at depth " + currentDepth());
                }
                startConditions(uri, localName, qName, attributes);
                break;
            case QNames.AUDIENCE_RESTRICTION:
                elementStack.push(AssertionElement.AudienceRestriction);
                startAudienceRestriction(uri, localName, qName, attributes);
                break;
            case QNames.AUDIENCE:
                elementStack.push(AssertionElement.Audience);
                break;
            case QNames.ATTRIBUTE_STATEMENT:
                elementStack.push(AssertionElement.AttributeStatement);
                startStatement(uri, localName, qName, attributes);
                break;
            case QNames.ATTRIBUTE:
                elementStack.push(AssertionElement.Attribute);
                startAttribute(uri, localName, qName, attributes);
                break;
            case QNames.ATTRIBUTE_VALUE:
                elementStack.push(AssertionElement.AttributeValue);
                break;
            default:
                signature.startElement(uri, localName, qName, attributes);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case QNames.CONDITIONS:
                this.conditions = new ConditionsImpl(notBefore, notOnOrAfter, audienceRestrictions);
                break;
            default:
                signature.endElement(uri, localName, qName);
                break;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (ch != null) {
             switch (currentElement()) {
                case Issuer:
                    issuerBuilder.append(String.valueOf(ch, start, length));
                    break;
                case Audience:
                    if (audienceRestrictions.isEmpty()) {
                        throw new IllegalStateException("cannot add Audience, have not seen any AudienceRestriction");
                    }
                    ((AudienceRestrictionImpl)audienceRestrictions
                        .get(audienceRestrictions.size() - 1))
                        .addAudience(String.valueOf(ch, start, length));
                    break;
                case AttributeValue:
                    if (attributeStatements.isEmpty()) {
                        throw new IllegalStateException("cannot add AttributeValue, have not seen any AttributeStatement");
                    }

                    final AttributeStatement statement = attributeStatements
                        .get(attributeStatements.size() - 1);
                    List<Attribute> attributes = statement.getAttributes();
                    if (attributes.isEmpty()) {
                        throw new IllegalStateException("cannot add AttributeValue, have not seen any Attribute");
                    }
                    ((AttributeImpl)attributes.get(attributes.size() - 1)).addValue(String.valueOf(ch, start, length));
                    break;

                default:
                    signature.characters(ch, start, length);
                    break;
            }
        }
    }

    private void startAttribute(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (attributeStatements.isEmpty()) {
            throw new IllegalStateException("cannot add Attribute, have not seen any AttributeStatement");
        }
        final String name = attributes.getValue(QNames.NAME);
        ((AttributeStatementImpl)attributeStatements
            .get(attributeStatements.size() - 1)).addAttribute(new AttributeImpl(name));
    }

    private void startStatement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        attributeStatements.add(new AttributeStatementImpl());
    }

    private void startAudienceRestriction(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        audienceRestrictions.add(new AudienceRestrictionImpl());
    }

    private void startConditions(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        notBefore = new GregorianCalendar();
        notBefore.setTime(
            DatatypeConverter.parseDateTime(attributes.getValue(QNames.NOT_BEFORE))
                .getTime());

        notOnOrAfter = new GregorianCalendar();
        notOnOrAfter.setTime(
            DatatypeConverter.parseDateTime(attributes.getValue(QNames.NOT_ON_OR_AFTER))
                .getTime());
    }

    private void startSubjectConfirmation(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        subjects.add(new SubjectImpl(attributes.getValue(QNames.METHOD)));
    }

    private void startAssertion(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        issueInstant = new GregorianCalendar();
        issueInstant.setTime(
            DatatypeConverter.parseDateTime(attributes.getValue(QNames.ISSUE_INSTANT))
                .getTime());

        version = attributes.getValue(QNames.VERSION);
        id = attributes.getValue(QNames.ID);
    }
}
