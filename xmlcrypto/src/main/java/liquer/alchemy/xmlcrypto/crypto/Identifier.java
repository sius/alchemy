package liquer.alchemy.xmlcrypto.crypto;

public final class Identifier {

    public static final String XMLNS = "xmlns";
    public static final String XMLNS_COLON = XMLNS + ':';

    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    public static final String XML_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    public static final String XML_SCHEMA_DTD_LOCATION = "http://www.w3.org/2001/XMLSchema.dtd";

    public static final String SOAP_NS_PREFIX = "soap";
    public static final String SOAP_SCHEMA_LOCATION = "http://www.w3.org/2003/05/soap-envelope";
    public static final String SOAP_ENV_SCHEMA_LOCATION ="http://schemas.xmlsoap.org/soap/envelope/";

    // Default Namespace for Prefix
    public static final String XMLDSIG_NS_PREFIX = "";
    public static final String XMLDSIG_NS_URI = "http://www.w3.org/2000/09/xmldsig#";
    public static final String XMLDSIG_SCHEMA_LOCATION = "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd";
    public static final String DEFAULT_NS_URI = XMLDSIG_NS_URI;

    public static final String XMLENC_NS_PREFIX = "xenc";
    public static final String XMLENC_NS_URI = "http://www.w3.org/2001/04/xmlenc#";
    public static final String XMLENC_SCHEMA_LOCATION = "http://www.w3.org/TR/2002/REC-xmlenc-core-20021210/xenc-schema.xsd";

    public static final String SAML2_NS_PREFIX = "saml";
    public static final String SAML2_NS_URI = "urn:oasis:names:tc:SAML:2.0:assertion";
    public static final String WSSE_NS_PREFIX = "wsse";
    public static final String WSSE_NS_SCHEMA_LOCATION = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String DATEV_WSSE_NS_SCHEMA_LOCATION = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd";
    public static final String WSU_NS_PREFIX = "wsu";
    public static final String WSU_NS_SCHEMA_LOCATION = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";


    /* Canonicalization (C14n) Algorithms */
    // Canonical XML 1.0 (omit comments)
    public static final String CANONICAL_XML_1_0_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    // Canonical XML 1.0 (with comments)
    public static final String CANONICAL_XML_1_0_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";

    // Exclusive XML Canonicalization 1.0 (omit comments)
    public static final String EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";

    // Exclusive XML Canonicalization 1.0 (with comments)
    public static final String EXCLUSIVE_CANONICAL_XML_1_0_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";

    // Canonical XML 1.1 (omit comments)
    public static final String CANONICAL_XML_1_1_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";

    // Canonical XML 1.1 (with comments)
    public static final String CANONICAL_XML_1_1_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";

    /* Transform */
    public static final String ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    /* DigestMethod Algorithms */
    // (Use is DISCOURAGED; see SHA-1 Warning)
    public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    public static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";

    /* Encoding/Transform*/
    public static final String BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";


    /* SignatureMethod Message Authentication Code Algorithms (MAC) */
    // Required

    // HMAC-SHA1 (Use is DISCOURAGED; see SHA-1 Warning)
    public static final String HMAC_SHA1 =  "http://www.w3.org/2000/09/xmldsig#hmac-sha1";

    // HMAC-SHA256
    public static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";

    // Recommended

    // HMAC-SHA384
    public static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";

    // HMAC-SHA512
    public static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";

    // Optional
    // HMAC-SHA224
    public static final String HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";

    /* SignatureMethod Public Key Signature Algorithms */
    // (signature verification; use for signature generation is DISCOURAGED; see SHA-1 Warning)
    public static final String RSA_WITH_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

    // [section 6.4.2 RSA (PKCS#1 v1.5)]
    public static final String RSA_WITH_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    public static final String RSA_WITH_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String RSA_WITH_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String RSA_WITH_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";

    /* More SignatureMethod Public Key Signature Algorithms */
    // Required
    // [section 6.4.3 ECDSA]
    public static final String ECDSA_WITH_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";

    // (signature verification / generation)
    // [section 6.4.1 DSA]
    public static final String DSA_WITH_SHA1 = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";

    // (Use is DISCOURAGED; see SHA-1 Warning)
    // [section 6.4.3 ECDSA]
    public static final String ECDSA_WITH_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    public static final String ECDSA_WITH_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    public static final String ECDSA_WITH_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    public static final String ECDSA_WITH_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";

    // (signature generation)
    // [section 6.4.1 DSA]
    public static final String DSA_WITH_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";

    /* Other */
    // XPath
    public static final String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    // XPath Filter 2.0
    public static final String XPATH_FILTER_2_0 = "http://www.w3.org/2002/06/xmldsig-filter2";

    // Optional
    public static final String XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";

    public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT = "-----END CERTIFICATE-----";

    private Identifier() {}
}
