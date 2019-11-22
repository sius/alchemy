package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSigner;

import java.util.Calendar;
import java.util.List;

public interface Assertion {

    String getId();

    String getVersion();

    Calendar getIssueInstant();

    String getIssuer();

    List<Subject> getSubjects();

    Conditions getConditions();

    List<AttributeStatement> getAttributeStatements();

    XmlSigner getXmlSigner();

    Signature getSignature();

    ValidationResult validateSignature();
}
