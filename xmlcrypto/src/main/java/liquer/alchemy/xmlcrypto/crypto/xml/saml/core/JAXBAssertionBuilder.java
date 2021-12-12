package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.CryptoSupport;
import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.*;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AssertionBuilder;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Base64GZipped;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import jakarta.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Builds signed and unsigned Assertion Strings.
 *
 * The rules for JAXB in a multi-threaded environment are very simple:
 * you can share the JAXBContext object among threads.
 * Doing so will also improve performance, as the construction of the context may be expensive.
 * All other objects, including Marshaller and Unmarshaller,
 * are not thread-safe and must not be shared.
 * The static helper methods in the JAXB class can be used from several threads, of course.
 * In practice, this means that if you need a JAXBContext instance,
 * you should probably store in a static member.
 */
final class JAXBAssertionBuilder implements AssertionBuilder {

    public static final String DEFAULT_VERSION = "2.0";
    public static final String DEFAULT_SUBJECT_CONFIRMATION_TYPE_METHOD =
        "urn:oasis:names:tc:SAML:2.0:cm:bearer";

    private static final Logger LOG = LoggerFactory.getLogger(SAXAssertionReader.class);
    private static final ThreadLocal<DatatypeFactory> DATATYPE_FACTORY_THREAD_LOCAL;
    private static final ThreadLocal<ObjectFactory> OBJECT_FACTORY_THREAD_LOCAL;
    private static final ThreadLocal<Marshaller> MARSHALLER_THREAD_LOCAL;
    private static final JAXBContext CONTEXT;


    static {
        DATATYPE_FACTORY_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                return DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException("failed to create " + DatatypeFactory.class.getSimpleName(), e);
            }
        });
        OBJECT_FACTORY_THREAD_LOCAL = ThreadLocal.withInitial(() -> new ObjectFactory());

        JAXBContext tempContext = null;
        try {
            tempContext = JAXBContext.newInstance(AssertionType.class);
        } catch (JAXBException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            CONTEXT = tempContext;
        }
        MARSHALLER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                return CONTEXT.createMarshaller();
            } catch (JAXBException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException("failed to create " + Marshaller.class.getSimpleName(), e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            DATATYPE_FACTORY_THREAD_LOCAL.remove();
            OBJECT_FACTORY_THREAD_LOCAL.remove();
            MARSHALLER_THREAD_LOCAL.remove();
        }));
    }

    private AssertionType assertion;

    public JAXBAssertionBuilder() {
        reset();
    }

    /**
     * Create an empty Assertion with its default attributes and the default Subject
     *
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder reset() {
        assertion = new AssertionType();
        return version().id().issueInstant().subject();
    }

    /**
     *
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder issueInstant() {
        return issueInstant(new GregorianCalendar());
    }

    /**
     *
     * @param calendar
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder issueInstant(GregorianCalendar calendar) {
        final DatatypeFactory df = DATATYPE_FACTORY_THREAD_LOCAL.get();
        assertion.setIssueInstant(df.newXMLGregorianCalendar(calendar == null ? new GregorianCalendar() : calendar));
        return this;
    }

    /**
     *
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder version() {
        return version(DEFAULT_VERSION);
    }

    /**
     * @param version
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder version(String version) {
        if (version != null) {
            assertion.setVersion(version);
        }
        return this;
    }

    /**
     * Generate random UUID and assign to Assertion ID attribute
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder id() {
        return id(new NCName());
    }

    /**
     * Assign ID attribute to Assertion
     *
     * @param id
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder id(NCName id) {
        if (id != null) {
            assertion.setID(id.toString());
        }
        return this;
    }

    /**
     * Assign ID attribute to Assertion
     *
     * This Method can be used to detect a vulnerable
     * SAML validation implementation, i.e.
     * that does not implement a Schema based structure validation
     * To detect a vulnerable Implementation you can provide an invalid NCName
     *
     * @param id
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder id(String id) {
        if (id != null) {
            assertion.setID(id);
        }
        return this;
    }

    /**
     * Assign issuerNameIdValue to Assertion
     *
     * @param issuerNameIdValue
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder issuer(String issuerNameIdValue) {
        final NameIDType nameIDType = new NameIDType();
        nameIDType.setValue(issuerNameIdValue);
        assertion.setIssuer(nameIDType);
        return this;
    }

    /**
     * Add Subject with DEFAULT_SUBJECT_CONFIRMATION_TYPE_METHOD
     *
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder subject() {
        return subject(DEFAULT_SUBJECT_CONFIRMATION_TYPE_METHOD);
    }
    /**
     * Add Subject, SubjectConfirmation to Assertion
     *
     * @param subjectConfirmationTypeMethod
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder subject(String subjectConfirmationTypeMethod) {
        return subject(new SubjectType(), subjectConfirmationTypeMethod);
    }

    /**
     * Add Subject, SubjectConfirmation to Assertion
     *
     * @param subject the SubjectType
     * @param subjectConfirmationTypeMethod SubjectConfirmation method attribute value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder subject(SubjectType subject, String subjectConfirmationTypeMethod) {
        final SubjectType finalSubject =
            subject == null ? new SubjectType() : subject;

        final SubjectConfirmationType subjectConfirmationType = new SubjectConfirmationType();
        subjectConfirmationType.setMethod(subjectConfirmationTypeMethod);
        final ObjectFactory of = OBJECT_FACTORY_THREAD_LOCAL.get();
        finalSubject.getContent().add(of.createSubjectConfirmation(subjectConfirmationType));
        assertion.setSubject(finalSubject);
        return this;
    }

    /**
     * Assign Conditions to Assertion with valid time in milliseconds
     *
     * @param milliseconds the validity time in milliseconds from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsMillis(int milliseconds, String audience) {
        return conditions(new GregorianCalendar(), Calendar.MILLISECOND, Math.max(1, milliseconds), audience);
    }

    /**
     * Assign Conditions to Assertion with valid time in seconds
     *
     * @param seconds the validity time in seconds from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsSeconds(int seconds, String audience) {
        return conditions(new GregorianCalendar(), Calendar.SECOND, Math.max(1, seconds), audience);
    }

    /**
     * Assign Conditions to Assertion with valid time in minutes
     * @param minutes the validity time in minutes from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsMinutes(int minutes, String audience) {
        return conditions(new GregorianCalendar(), Calendar.MINUTE, Math.max(1, minutes), audience);
    }

    /**
     * Assign Conditions to Assertion with valid time in minutes
     *
     * @param hours the validity time in hours from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsHours(int hours, String audience) {
        return conditions(new GregorianCalendar(), Calendar.HOUR, Math.max(1, hours), audience);
    }

    /**
     * Assign Conditions to Assertion with valid time in days
     *
     * @param days the validity time in days from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsDays(int days, String audience) {
        return conditions(new GregorianCalendar(), Calendar.DATE, Math.max(1, days), audience);
    }

    /**
     * Assign Conditions to Assertion with valid time in years
     *
     * @param years the validity time in years from now (min value 1)
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditionsYears(int years, String audience) {
        return conditions(new GregorianCalendar(), Calendar.YEAR, Math.max(1, years), audience);
    }
    /**
     * Assign Conditions to Assertion with valid time in calendarDurationTimeUnit
     *
     * @param calendarDurationTimeUnit the Calendar duration time unit field
     * @param durationValue The duration amount in Calendar duration time unit
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditions(int calendarDurationTimeUnit, int durationValue, String audience) {
        return conditions(new GregorianCalendar(), calendarDurationTimeUnit, durationValue, audience);
    }

    /**
     * Assign Conditions to Assertion
     *
     * @param notBefore Start time of validity
     * @param calendarDurationTimeUnit the Calendar duration time unit field
     * @param durationValue The duration amount in Calendar duration time unit
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditions(GregorianCalendar notBefore, int calendarDurationTimeUnit, int durationValue, String audience) {
        final ConditionsType conditions = new ConditionsType();
        final GregorianCalendar finalNotBefore
            = notBefore == null ? new GregorianCalendar() : notBefore;
        final GregorianCalendar notOnOrAfter = new GregorianCalendar();
        final AudienceRestrictionType audienceRestriction = new AudienceRestrictionType();
        notOnOrAfter.setTime(finalNotBefore.getTime());
        notOnOrAfter.add(calendarDurationTimeUnit, durationValue);
        return conditions(conditions, finalNotBefore, notOnOrAfter, audience);
    }

    /**
     * Assign Conditions to Assertion
     *
     * @param conditions the Conditions instance
     * @param notBefore Start time of validity
     * @param notOnOrAfter End time of validity
     * @param audience the audience value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder conditions(
            ConditionsType conditions,
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            String audience) {

        final ConditionsType finalConditions =
            conditions == null ? new ConditionsType() : conditions;

        final GregorianCalendar finalNotBefore =
            notBefore == null ? new GregorianCalendar() : notBefore;

        final GregorianCalendar finalNotOnOrAfter =
            notOnOrAfter == null ? new GregorianCalendar() : notOnOrAfter;

        final AudienceRestrictionType audienceRestriction = new AudienceRestrictionType();
        audienceRestriction.getAudience().add(audience);
        final DatatypeFactory df = DATATYPE_FACTORY_THREAD_LOCAL.get();
        finalConditions.setNotBefore(df.newXMLGregorianCalendar(finalNotBefore));
        finalConditions.setNotOnOrAfter(df.newXMLGregorianCalendar(finalNotOnOrAfter));
        finalConditions.getConditionOrAudienceRestrictionOrOneTimeUse().add(audienceRestriction);
        assertion.setConditions(finalConditions);
        return this;
    }

    /**
     * Add AttributeStatement, Attribute to Assertion
     * @param attributes the AttributeType list
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder addStatement(List<AttributeType> attributes) {
        return addStatement(new AttributeStatementType(), attributes);
    }

    /**
     * Add AttributeStatement, Attribute to Assertion
     * @param attributeName the AttributeType name
     * @param attributeValues the AttributeType value
     * @return the partial build AssertionBuilder
     */
    @Override
    public AssertionBuilder addStatement(String attributeName, List<String> attributeValues) {
        final AttributeType attribute = new AttributeType();
        attribute.setName(attributeName);
        attribute.getAttributeValue().addAll(attributeValues);
        return addStatement(new AttributeStatementType(), Arrays.asList(attribute));
    }

    /**
     * Add AttributeStatement, Attribute to Assertion
     * @param statement the AttributeStatementType instance
     * @param attributes the AttributeType list
     * @return
     */
    @Override
    public AssertionBuilder addStatement(AttributeStatementType statement, List<AttributeType> attributes) {
        final AttributeStatementType finalStatement =
            statement == null ? new AttributeStatementType() : statement;

        finalStatement.getAttributeOrEncryptedAttribute().addAll(attributes);

        assertion.getStatementOrAuthnStatementOrAuthzDecisionStatement().add(finalStatement);
        return this;
    }

    /**
     * Build the unsigned String representation of the Assertion
     *
     * @return the unsigned Assertion without Signature and KeyInfo
     */
    @Override
    public String build() {
        try {
            // 1 Render XML to Document
            final Document assertionDoc = XmlSupport.newDocument();
            final ObjectFactory of = OBJECT_FACTORY_THREAD_LOCAL.get();
            final JAXBElement<AssertionType> assertionElement = of.createAssertion(assertion);
            final Marshaller marshaller = MARSHALLER_THREAD_LOCAL.get();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            marshaller.marshal(assertionElement, assertionDoc);

            return XmlSupport.stringify(assertionDoc);

        } catch (JAXBException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Build the signed String representation of the Assertion
     * @param xmlSignerOptions the XmlSignerOptions containing:
     *                         - canonicalizationAlgorithm:
     *                              the transform algorithm to apply to the sign data
     *                         - digestAlgorithm:
     *                              the digest algorithm to apply to the sign data to create a SignInfo
     *                         - signatureAlgorithm:
     *                               the signature algorithm to apply to the SignInfo
     * @param publicKeyInfo the public key to provide inside the Assertion Envelope
     * @param privateKeyInfo the private key used for the signature algorithm
     * @return
     */
    @Override
    public String buildSigned(XmlSignerOptions xmlSignerOptions,
                              KeyInfo publicKeyInfo,
                              KeyInfo privateKeyInfo) {
        return buildSigned(
            xmlSignerOptions,
            publicKeyInfo,
            privateKeyInfo,
            true,
            false,
            false);
    }

    /**
     * Build the signed Base64 encoded GZipped representation of the Assertion
     *
     * @param xmlSignerOptions the XmlSignerOptions containing:
     *                         - canonicalizationAlgorithm:
     *                              the transform algorithm to apply to the sign data
     *                         - digestAlgorithm:
     *                              the digest algorithm to apply to the sign data to create a SignInfo
     *                         - signatureAlgorithm:
     *                              the signature algorithm to apply to the SignInfo
     * @param publicKeyInfo the public key to provide inside the Assertion Envelope
     * @param privateKeyInfo the private key used for the signature algorithm
     * @return the Base64 encoded GZipped Assertion String
     */
    @Override
    public String buildBase64GZippedToken(XmlSignerOptions xmlSignerOptions,
                                          KeyInfo publicKeyInfo,
                                          KeyInfo privateKeyInfo) {
        return buildSigned(
            xmlSignerOptions,
            publicKeyInfo,
            privateKeyInfo,
            true,
            false,
            true);

    }

    /**
     * Build the signed String representation of the Assertion
     *
     * @param xmlSignerOptions the XmlSignerOptions containing:
     *                         - canonicalizationAlgorithm:
     *                              the transform algorithm to apply to the sign data
     *                         - digestAlgorithm:
     *                              the digest algorithm to apply to the sign data to create a SignInfo
     *                         - signatureAlgorithm:
     *                              the signature algorithm to apply to the SignInfo
     * @param publicKeyInfo the public key to provide inside the Assertion Envelope
     * @param privateKeyInfo the private key used for the signature algorithm
     * @param fragment the XML Prolog will be omitted if true (Default: true)
     * @param formattedOutput pretty printing if true
     * @param base64GZipped build a Base64 encoded GZipped Assertion String to return
     * @return the signed Assertion String
     */
    @Override
    public String buildSigned(XmlSignerOptions xmlSignerOptions,
                              KeyInfo publicKeyInfo,
                              KeyInfo privateKeyInfo,
                              boolean fragment,
                              boolean formattedOutput,
                              boolean base64GZipped) {

        final XmlReference xmlReference = new XmlReference();
        xmlReference.setDigestAlgorithm(xmlSignerOptions.getDigestAlgorithm());
        xmlReference.setTransforms(
            Arrays.asList(
                Identifier.ENVELOPED_SIGNATURE,
                xmlSignerOptions.getCanonicalizationAlgorithm()));
        xmlReference.setXpathExpression("//*[local-name(.)='Assertion']");

        return buildSigned(
            xmlReference,
            xmlSignerOptions.getSignatureAlgorithm(),
            publicKeyInfo,
            privateKeyInfo,
            fragment,
            formattedOutput,
            base64GZipped);
    }


    /**
     * Build the signed String representation of the Assertion
     *
     * @param xmlReference the Reference with the XPathExpression of the Xml Node to sign
     *                      and the necessary transforms to generate the SignInfo
     * @param signatureAlgorithm the signature algorithm to apply to the SignInfo
     * @param publicKeyInfo the public key to provide inside the Assertion Envelope
     * @param privateKeyInfo the private key used for the signature algorithm
     * @param fragment the XML Prolog will be omitted if true (Default: true)
     * @param formattedOutput pretty printing if true
     * @param base64GZipped create a Base64 encoded GZipped Assertion String to return
     * @return the the signed Assertion String
     */
    @Override
    public String buildSigned(XmlReference xmlReference,
                              String signatureAlgorithm,
                              KeyInfo publicKeyInfo,
                              KeyInfo privateKeyInfo,
                              boolean fragment,
                              boolean formattedOutput,
                              boolean base64GZipped) {

        // Create XmlSigner and compute SignatureXml
        final XmlSignerOptions xmlSignerOptions = new XmlSignerOptions();
        xmlSignerOptions.setSignatureAlgorithm(signatureAlgorithm);
        final XmlSigner xmlSigner = new XmlSigner(xmlSignerOptions);
        xmlSigner.setSigningKey(privateKeyInfo.getKey());
        xmlSigner.addReference(xmlReference);

        return buildSigned(xmlSigner, publicKeyInfo, fragment, formattedOutput, base64GZipped);
    }

    /**
     * Build the signed String representation of the Assertion
     *
     * @param xmlSigner the fully configured XmlSigner
     * @param publicKeyInfo the public Key to provide inside the Assertion Envelope
     * @param fragment the XML Prolog will be omitted if true (Default: true)
     * @param formattedOutput pretty printing if true
     * @param base64GZipped create a Base64 encoded GZipped Assertion String to return
     * @return the signed Assertion String
     */
    @Override
    public String buildSigned(XmlSigner xmlSigner,
                              KeyInfo publicKeyInfo,
                              boolean fragment,
                              boolean formattedOutput,
                              boolean base64GZipped) {
        try {
            xmlSigner.computeSignature(build());

            // 1 Un-marshall signatureXml Node to JAXB SignatureType
            final Node signatureXmlNode = XmlSupport.toDocument(xmlSigner.getSignatureXml());
            signatureXmlNode.getFirstChild().getAttributes().removeNamedItem("xmlns");

            final JAXBContext jaxbContext = JAXBContext.newInstance(SignatureType.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final JAXBElement<SignatureType> signatureElement =
                jaxbUnmarshaller.unmarshal(signatureXmlNode, SignatureType.class);

            // 2 Update Assertion Signature with KeyInfo and Signature
            final SignatureType signature = signatureElement.getValue();
            final KeyInfoType keyInfo = new KeyInfoType();

            signature.setKeyInfo(keyInfo);
            assertion.setSignature(signature);

            // 2.1 Generate KeyInfo
            X509Certificate cert = CryptoSupport.readX509Certificate(publicKeyInfo.getKey().getBytes());

            final X509IssuerSerialType x509IssuerSerial = new X509IssuerSerialType();
            x509IssuerSerial.setX509IssuerName(cert.getIssuerX500Principal().getName());
            x509IssuerSerial.setX509SerialNumber(cert.getSerialNumber());

            final ObjectFactory of = OBJECT_FACTORY_THREAD_LOCAL.get();
            final X509DataType x509Data = new X509DataType();
            x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(
                of.createX509DataTypeX509Certificate(cert.getEncoded()));
            x509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(
                of.createX509DataTypeX509IssuerSerial(x509IssuerSerial));
            keyInfo.getContent().add(of.createX509Data(x509Data));

            // 3 Render formatted signed Assertion
            final Document signedAssertionDoc = XmlSupport.newDocument();
            final JAXBElement<AssertionType> assertionElement = of.createAssertion(assertion);
            final Marshaller marshaller = MARSHALLER_THREAD_LOCAL.get();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(fragment));
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formattedOutput));
            marshaller.marshal(assertionElement, signedAssertionDoc);

            final String ret = XmlSupport.stringify(signedAssertionDoc);
            return (base64GZipped)
                ? Base64GZipped.of(ret)
                : ret;

        } catch (JAXBException
            | CertificateException
            | IOException
            | SAXException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
