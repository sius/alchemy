package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

public final class AssertionQNames {
    public static final String NS_PREFIX = "saml:";

    public static final String ASSERTION = NS_PREFIX + AssertionLocalNames.ASSERTION;
    public static final String ISSUER = NS_PREFIX + AssertionLocalNames.ISSUER;
    public static final String SUBJECT = NS_PREFIX + AssertionLocalNames.SUBJECT;
    public static final String SUBJECT_CONFIRMATION = NS_PREFIX + AssertionLocalNames.SUBJECT_CONFIRMATION;
    public static final String CONDITIONS = NS_PREFIX + AssertionLocalNames.CONDITIONS;
    public static final String AUDIENCE_RESTRICTION = NS_PREFIX + AssertionLocalNames.AUDIENCE_RESTRICTION;
    public static final String AUDIENCE = NS_PREFIX + AssertionLocalNames.AUDIENCE;
    public static final String ATTRIBUTE_STATEMENT = NS_PREFIX + AssertionLocalNames.ATTRIBUTE_STATEMENT;
    public static final String ATTRIBUTE = NS_PREFIX + AssertionLocalNames.ATTRIBUTE;
    public static final String ATTRIBUTE_VALUE = NS_PREFIX + AssertionLocalNames.ATTRIBUTE_VALUE;
    public static final String ISSUE_INSTANT = "IssueInstant";
    public static final String VERSION = "Version";
    public static final String ID = "ID";
    public static final String METHOD = "Method";
    public static final String NOT_BEFORE = "NotBefore";
    public static final String NOT_ON_OR_AFTER = "NotOnOrAfter";
    public static final String NAME = "Name";// Signature
    public static final String SIGNATURE = "Signature";
    public static final String CANONICALIZATION_METHOD = "CanonicalizationMethod";
    public static final String SIGNATURE_METHOD = "SignatureMethod";
    public static final String SIGNED_INFO = "SignedInfo";
    public static final String REFERENCE = "Reference";
    public static final String DIGEST_METHOD = "DigestMethod";
    public static final String DIGEST_VALUE = "DigestValue";
    public static final String TRANSFORMS = "Transforms";
    public static final String TRANSFORM = "Transform";
    public static final String INCLUSIVE_NAMESPACES = "InclusiveNamespaces";
    public static final String X509_DATA = "X509Data";
    public static final String X509_CERTIFICATE = "X509Certificate";
    public static final String X509_ISSUER_SERIAL = "X509IssuerSerial";
    public static final String X509_ISSUER_NAME = "X509IssuerName";
    public static final String X509_SERIAL_NUMBER = "X509SerialNumber";
    public static final String SIGNATURE_VALUE = "SignatureValue";
    public static final String KEY_INFO = "KeyInfo";
    public static final String ALGORITHM = "Algorithm";
    public static final String URI = "URI";
    public static final String PREFIX_LIST = "PrefixList";

    private AssertionQNames() { }
}