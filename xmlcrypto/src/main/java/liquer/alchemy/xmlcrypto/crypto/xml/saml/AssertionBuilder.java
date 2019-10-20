package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.KeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlReference;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSigner;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.AttributeStatementType;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.AttributeType;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.ConditionsType;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.SubjectType;

import java.util.GregorianCalendar;
import java.util.List;

public interface AssertionBuilder {
    String DEFAULT_VERSION = "2.0";
    String DEFAULT_SUBJECT_CONFIRMATION_TYPE_METHOD =
        "urn:oasis:names:tc:SAML:2.0:cm:bearer";

    AssertionBuilder reset();

    AssertionBuilder issueInstant();

    AssertionBuilder issueInstant(GregorianCalendar calendar);

    AssertionBuilder version();

    AssertionBuilder version(String version);

    AssertionBuilder id();

    AssertionBuilder id(String id);

    AssertionBuilder issuer(String issuerNameIdValue);

    AssertionBuilder subject();

    AssertionBuilder subject(String subjectConfirmationTypeMethod);

    AssertionBuilder subject(SubjectType subject, String subjectConfirmationTypeMethod);

    AssertionBuilder conditionsMillis(int milliseconds, String audience);

    AssertionBuilder conditionsSeconds(int seconds, String audience);

    AssertionBuilder conditionsMinutes(int minutes, String audience);

    AssertionBuilder conditionsHours(int hours, String audience);

    AssertionBuilder conditionsDays(int days, String audience);

    AssertionBuilder conditionsYears(int years, String audience);

    AssertionBuilder conditions(int calendarDurationTimeUnit, int durationValue, String audience);

    AssertionBuilder conditions(GregorianCalendar notBefore, int calendarDurationTimeUnit, int durationValue, String audience);

    AssertionBuilder conditions(
            ConditionsType conditions,
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            String audience);

    AssertionBuilder addStatement(List<AttributeType> attributes);

    AssertionBuilder addStatement(String attributeName, List<String> attributeValues);

    AssertionBuilder addStatement(AttributeStatementType statement, List<AttributeType> attributes);

    String build();

    String buildSigned(XmlSignerOptions xmlSignerOptions,
                       KeyInfo publicKeyInfo,
                       KeyInfo privateKeyInfo);

    String buildBase64GZippedToken(XmlSignerOptions xmlSignerOptions,
                                   KeyInfo publicKeyInfo,
                                   KeyInfo privateKeyInfo);

    String buildSigned(XmlSignerOptions xmlSignerOptions,
                       KeyInfo publicKeyInfo,
                       KeyInfo privateKeyInfo,
                       boolean fragment,
                       boolean formattedOutput,
                       boolean base64GZipped);

    String buildSigned(XmlReference xmlReference,
                       String signatureAlgorithm,
                       KeyInfo publicKeyInfo,
                       KeyInfo privateKeyInfo,
                       boolean fragment,
                       boolean formattedOutput,
                       boolean base64GZipped);

    String buildSigned(XmlSigner xmlSigner,
                       KeyInfo publicKeyInfo,
                       boolean fragment,
                       boolean formattedOutput,
                       boolean base64GZipped);
}
