package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.*;
import liquer.alchemy.xmlcrypto.crypto.xml.core.KeyInfoImpl;
import liquer.alchemy.xmlcrypto.crypto.xml.core.X509DataImpl;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.*;
import liquer.alchemy.xmlcrypto.functional.Func0;
import liquer.alchemy.xmlcrypto.functional.Func1;
import liquer.alchemy.xmlcrypto.functional.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Reads Assertion with SAX
 * Resolve Schemas, DTDs from trusted locations
 */
final class SAXAssertionReader extends DefaultHandler implements Assertion, LSResourceResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SAXAssertionReader.class);

    private static Map<String, String> RESOLVER_MAP;

    static {
        RESOLVER_MAP = new HashMap<>();
        RESOLVER_MAP.put(Identifier.SAML2_NS_URI, "/schemas/oasis/saml/saml-schema-assertion-2.0.xsd");
        RESOLVER_MAP.put("datatypes.dtd", "/schemas/www.w3.org/2001/datatypes.dtd");
        RESOLVER_MAP.put(Identifier.XML_SCHEMA_DTD_LOCATION, "/schemas/www.w3.org/2001/XMLSchema.dtd");
        RESOLVER_MAP.put(Identifier.XMLDSIG_SCHEMA_LOCATION, "/schemas/www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd");
        RESOLVER_MAP.put(Identifier.SOAP_SCHEMA_LOCATION, "/schemas/www.w3.org/2003/05/soap-envelope/index.xsd");
        RESOLVER_MAP.put(Identifier.SOAP_ENV_SCHEMA_LOCATION, "/schemas/schemas.xmlsoap.org/soap/envelope/index.xsd");
        RESOLVER_MAP.put(Identifier.XMLENC_SCHEMA_LOCATION, "/schemas/www.w3.org/TR/2002/REC-xmlenc-core-20021210/xenc-schema.xsd");
        RESOLVER_MAP.put(Identifier.WSSE_NS_SCHEMA_LOCATION, "/schemas/oasis/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        RESOLVER_MAP.put(Identifier.DATEV_WSSE_NS_SCHEMA_LOCATION, "/schemas/oasis/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        RESOLVER_MAP.put(Identifier.WSU_NS_SCHEMA_LOCATION, "/schemas/oasis/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
    }

    private static void renderEndElement(final StringBuilder b, String uri, String localName, String qName) throws SAXException {
        // element name
        String elementName = localName;
        if ("".equals(elementName)) {
            // not namespaceAware
            elementName = qName;
        }
        b.append("</" + elementName + '>');
    }

    private static void renderStartElement(final StringBuilder b, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // element name
        String elementName = localName;
        if ("".equals(elementName)) {
            // not namespaceAware
            elementName = qName;
        }
        b.append('<' + elementName);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                // Attr name
                String aName = attributes.getLocalName(i);
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
        private String signatureValue;
        private String canonicalizationAlgorithm;
        private String signatureAlgorithm;
        private String digestAlgorithm;
        private String referenceURI;
        private KeyInfo keyInfo;
        private String digestValue;

        private StringBuilder signatureFragmentBuilder;
        private StringBuilder digestValueBuilder;
        private StringBuilder signatureValueBuilder;
        private StringBuilder x509CertificateBuilder;
        private StringBuilder x509IssuerNameBuilder;
        private StringBuilder x509SerialNumberBuilder;
        private boolean renderMode;

        // trick sonar and avoid if...else...
        private final Func0<AssertionElement> currentElementClosure;
        private final Func0<AssertionElement> popElementClosure;
        private final Func1<AssertionElement, AssertionElement> pushElementClosure;

        private SAXSignatureReader(final SAXAssertionReader assertionReader, XmlSignerOptions options) {
            this.signatureXml = null;
            this.implicitTransforms = options.getImplicitTransforms();
            this.transforms = new ArrayList<>();
            this.inclusiveNamespacesPrefixList = new ArrayList<>();
            this.references = new ArrayList<>();
            // use assertionReader elementStack
            this.currentElementClosure = assertionReader::currentElement;
            this.popElementClosure = assertionReader::popElement;
            this.pushElementClosure = assertionReader::pushElement;
        }

        SAXSignatureReader(String xml, XmlSignerOptions options) {
            this.signatureXml = xml;
            this.implicitTransforms = options.getImplicitTransforms();
            this.transforms = new ArrayList<>();
            this.inclusiveNamespacesPrefixList = new ArrayList<>();
            this.references = new ArrayList<>();

            final Stack<AssertionElement> elementStack = new Stack<>();
            this.currentElementClosure = elementStack::peek;
            this.popElementClosure = elementStack::pop;
            this.pushElementClosure = elementStack::push;
        }

        @Override public String toString() { return signatureXml == null ? this.signatureFragment : this.signatureXml; }

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
        public XmlReference getAssertionReference() { return this.references.isEmpty() ? null : this.references.get(0); }

        @Override
        public KeyInfo getKeyInfo() { return this.keyInfo; }

        private AssertionElement currentElement() { return currentElementClosure.apply(); }

        private AssertionElement pushElement(AssertionElement item) { return pushElementClosure.apply(item); }

        private AssertionElement popElement() { return popElementClosure.apply(); }

        /* ErrorHandler Implementation */

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            LOG.warn(exception.getMessage(), exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOG.error(exception.getMessage(), exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.error("FATAL: " + exception.getMessage(), exception);
        }

        /* End of ErrorHandler Implementation */

        /* ContentHandler Implementation */

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
            this.signatureFragment = signatureFragmentBuilder.toString();
            this.digestValue = digestValueBuilder.toString();
            this.signatureValue = signatureValueBuilder.toString();
            this.x509Certificate = x509CertificateBuilder.toString();
            this.x509IssuerName = x509IssuerNameBuilder.toString();
            this.x509SerialNumber = x509SerialNumberBuilder.toString();

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

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {

                case AssertionQNames.SIGNATURE:
                    this.pushElement(AssertionElement.SIGNATURE);
                    this.renderMode = true;
                    break;
                case AssertionQNames.CANONICALIZATION_METHOD:
                    this.pushElement(AssertionElement.CANONICALIZATION_METHOD);
                    this.canonicalizationAlgorithm = attributes.getValue(AssertionQNames.ALGORITHM);
                    break;
                case AssertionQNames.SIGNATURE_METHOD:
                    this.pushElement(AssertionElement.SIGNATURE_METHOD);
                    this.signatureAlgorithm = attributes.getValue(AssertionQNames.ALGORITHM);
                    break;
                case AssertionQNames.SIGNED_INFO:
                    this.pushElement(AssertionElement.SIGNED_INFO);
                    break;
                case AssertionQNames.REFERENCE:
                    this.pushElement(AssertionElement.REFERENCE);
                    this.referenceURI = attributes.getValue(AssertionQNames.URI);
                    break;
                case AssertionQNames.DIGEST_METHOD:
                    this.pushElement(AssertionElement.DIGEST_METHOD);
                    this.digestAlgorithm = attributes.getValue(AssertionQNames.ALGORITHM);
                    break;
                case AssertionQNames.DIGEST_VALUE:
                    this.pushElement(AssertionElement.DIGEST_VALUE);
                    break;
                case AssertionQNames.SIGNATURE_VALUE:
                    this.pushElement(AssertionElement.SIGNATURE_VALUE);
                    break;
                case AssertionQNames.TRANSFORMS:
                    this.pushElement(AssertionElement.TRANSFORMS);
                    break;
                case AssertionQNames.TRANSFORM:
                    this.pushElement(AssertionElement.TRANSFORM);
                    this.transforms.add(attributes.getValue(AssertionQNames.ALGORITHM));
                    break;
                case AssertionQNames.INCLUSIVE_NAMESPACES:
                    this.pushElement(AssertionElement.INCLUSIVE_NAMESPACES);
                    this.inclusiveNamespacesPrefixList.add(attributes.getValue(AssertionQNames.PREFIX_LIST));
                    break;
                case AssertionQNames.KEY_INFO:
                    this.pushElement(AssertionElement.KEY_INFO);
                    break;
                case AssertionQNames.X509_DATA:
                    this.pushElement(AssertionElement.X_509_DATA);
                    break;
                case AssertionQNames.X509_CERTIFICATE:
                    this.pushElement(AssertionElement.X_509_CERTIFICATE);
                    break;
                case AssertionQNames.X509_ISSUER_SERIAL:
                    this.pushElement(AssertionElement.X_509_ISSUER_SERIAL);
                    break;
                case AssertionQNames.X509_ISSUER_NAME:
                    this.pushElement(AssertionElement.X_509_ISSUER_NAME);
                    break;
                case AssertionQNames.X509_SERIAL_NUMBER:
                    this.pushElement(AssertionElement.X_509_SERIAL_NUMBER);
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
                case AssertionQNames.REFERENCE:
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
                case AssertionQNames.SIGNATURE:
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
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            if (this.renderMode) {
                this.signatureFragmentBuilder.append(String.valueOf(ch, start, length));
            }
        }



        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            switch (this.currentElement()) {
                case SIGNATURE:
                    if (this.renderMode) {
                        this.signatureFragmentBuilder.append(String.valueOf(ch, start, length));
                    }
                    break;
                case SIGNATURE_VALUE:
                    this.signatureValueBuilder.append(String.valueOf(ch, start, length));
                    break;
                case DIGEST_VALUE:
                    this.digestValueBuilder.append(String.valueOf(ch, start, length));
                    break;
                case X_509_CERTIFICATE:
                    this.x509CertificateBuilder.append(String.valueOf(ch, start, length));
                    break;
                case X_509_ISSUER_NAME:
                    this.x509IssuerNameBuilder.append(String.valueOf(ch, start, length));
                    break;
                case X_509_SERIAL_NUMBER:
                    this.x509SerialNumberBuilder.append(String.valueOf(ch, start, length));
                    break;
                default:
                    break;
            }
            if (this.renderMode) {
                this.signatureFragmentBuilder.append(String.valueOf(ch, start, length));
            }
        }

        /* End ContentHandler Implementation */
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
    public Signature getSignature() { return signature; }

    @Override
    public String getVersion() { return version; }

    @Override
    public String getId() { return id; }

    @Override
    public GregorianCalendar getIssueInstant() { return issueInstant; }

    @Override
    public String getIssuer() { return issuerBuilder.toString(); }

    @Override
    public List<Subject> getSubjects() { return Collections.unmodifiableList(subjects); }

    @Override
    public Conditions getConditions() { return conditions; }

    @Override
    public List<AttributeStatement> getAttributeStatements() { return Collections.unmodifiableList(attributeStatements); }

    @Override
    public XmlSigner getXmlSigner() { return xmlSigner; }

    @Override
    public ValidationResult validateSignature() { return xmlSigner.validateAssertion(this); }

    @Override
    public String toString() { return signedXml;  }

    private final SAXSignatureReader signature;

    private final Stack<AssertionElement> elementStack;

    /**
     * Reuse the EntityResolver and/or ErrorHandler
     */
    SAXAssertionReader() {
        signature = null;
        elementStack = null;
        signedXml = null;
        xmlSigner = null;
        audienceRestrictions = null;
        attributeStatements = null;
        subjects = null;
    }

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

    /* LSResourceResolver Implementation  */

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        final InputSource inputSource = require(systemId);
        final LSInput ret =  new DOMInputImpl(
            publicId,
            systemId,
            null,
            (inputSource != null ? inputSource.getByteStream() : null),
            null
        );
        ret.setBaseURI(baseURI);
        ret.setCertifiedText(true);
        return ret;
    }

    /* End LSResourceResolver Implementation */

    /* EntityResolver Implementation  */

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return require(systemId);
    }

    String resolveAbsolutePath(URI systemIdURI) {
        if (systemIdURI == null) {
            return null;
        }
        if (systemIdURI.getScheme() == null || systemIdURI.getScheme().equals("file")) {
            String[] segments = systemIdURI.getPath().split("/");
            return (segments.length > 0)
                ? RESOLVER_MAP.getOrDefault(segments[segments.length -1], null)
                : null;
        } else {
            return RESOLVER_MAP.getOrDefault(systemIdURI.toString(), null);
        }
    }


    InputSource require(String systemId) {
        final URI systemIdURI = Try.of(() -> new URI(systemId)).orElseGet(null);
        final String resourcePath = resolveAbsolutePath(systemIdURI);
        return resourcePath != null
            ? Try.of(() -> new InputSource(SAXAssertionReader.class.getResourceAsStream(resourcePath))).orElseGet(null)
            : null;
    }

    /* End of EntityResolver Implementation */

    /* ErrorHandler Implementation */

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        LOG.warn(exception.getMessage(), exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        LOG.error(exception.getMessage(), exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        LOG.error("FATAL: " + exception.getMessage(), exception);
    }

    /* End of ErrorHandler Implementation */

    private AssertionElement currentElement() {
        return elementStack.peek();
    }

    private AssertionElement pushElement(AssertionElement item) {
        return elementStack.push(item);
    }

    private AssertionElement popElement() {
        return elementStack.pop();
    }

    /* ContentHandler Implementation */

    @Override
    public void endDocument() {
        this.signature.endDocument();
        if (this.getSignature() != null) {
            this.xmlSigner.loadSignature(this.getSignature());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        switch (qName) {
            case AssertionQNames.ASSERTION:
                elementStack.push(AssertionElement.ASSERTION);
                startAssertion(uri, localName, qName, attributes);
                break;
            case AssertionQNames.ISSUER:
                elementStack.push(AssertionElement.ISSUER);
                break;
            case AssertionQNames.SUBJECT_CONFIRMATION:
                elementStack.push(AssertionElement.SUBJECT_CONFIRMATION);
                startSubjectConfirmation(uri, localName, qName, attributes);
                break;
            case AssertionQNames.CONDITIONS:
                elementStack.push(AssertionElement.CONDITIONS);
                startConditions(uri, localName, qName, attributes);
                break;
            case AssertionQNames.AUDIENCE_RESTRICTION:
                elementStack.push(AssertionElement.AUDIENCE_RESTRICTION);
                startAudienceRestriction(uri, localName, qName, attributes);
                break;
            case AssertionQNames.AUDIENCE:
                elementStack.push(AssertionElement.AUDIENCE);
                break;
            case AssertionQNames.ATTRIBUTE_STATEMENT:
                elementStack.push(AssertionElement.ATTRIBUTE_STATEMENT);
                startStatement(uri, localName, qName, attributes);
                break;
            case AssertionQNames.ATTRIBUTE:
                elementStack.push(AssertionElement.ATTRIBUTE);
                startAttribute(uri, localName, qName, attributes);
                break;
            case AssertionQNames.ATTRIBUTE_VALUE:
                elementStack.push(AssertionElement.ATTRIBUTE_VALUE);
                break;
            default:
                signature.startElement(uri, localName, qName, attributes);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case AssertionQNames.CONDITIONS:
                this.conditions = new ConditionsImpl(notBefore, notOnOrAfter, audienceRestrictions);
                break;
            default:
                signature.endElement(uri, localName, qName);
                break;
        }

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (ch != null) {
            signature.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (ch != null) {
             switch (currentElement()) {
                case ISSUER:
                    issuerBuilder.append(String.valueOf(ch, start, length));
                    break;
                case AUDIENCE:
                    ((AudienceRestrictionImpl)audienceRestrictions
                        .get(audienceRestrictions.size() - 1))
                        .addAudience(String.valueOf(ch, start, length));
                    break;
                case ATTRIBUTE_VALUE:
                    final AttributeStatement statement = attributeStatements
                        .get(attributeStatements.size() - 1);
                    List<Attribute> attributes = statement.getAttributes();
                    ((AttributeImpl)attributes.get(attributes.size() - 1)).addValue(String.valueOf(ch, start, length));
                    break;

                default:
                    signature.characters(ch, start, length);
                    break;
            }
        }
    }

    private void startAttribute(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final String name = attributes.getValue(AssertionQNames.NAME);
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
            DatatypeConverter.parseDateTime(attributes.getValue(AssertionQNames.NOT_BEFORE))
                .getTime());

        notOnOrAfter = new GregorianCalendar();
        notOnOrAfter.setTime(
            DatatypeConverter.parseDateTime(attributes.getValue(AssertionQNames.NOT_ON_OR_AFTER))
                .getTime());
    }

    private void startSubjectConfirmation(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        subjects.add(new SubjectImpl(attributes.getValue(AssertionQNames.METHOD)));
    }

    private void startAssertion(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        issueInstant = new GregorianCalendar();
        issueInstant.setTime(
            DatatypeConverter.parseDateTime(attributes.getValue(AssertionQNames.ISSUE_INSTANT))
                .getTime());

        version = attributes.getValue(AssertionQNames.VERSION);
        id = attributes.getValue(AssertionQNames.ID);
    }

    /* End ContentHandler Implementation */
}
