package liquer.alchemy.crypto.saml;

import liquer.alchemy.crypto.xml.XmlSigner;

import java.util.List;

public interface Assertion {

    DateTime getIssueInstant();

    String getIssuer();

    Subject getSubject();

    Conditions getConditions();

    List<AttributeStatement> getAttributeStatements();

    Signature getSignature();

    XmlSigner getXmlSigner();

    SamlVerificationResult verifySignature(String signature);
}
