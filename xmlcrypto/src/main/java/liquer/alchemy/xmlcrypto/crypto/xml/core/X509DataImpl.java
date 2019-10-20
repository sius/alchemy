package liquer.alchemy.xmlcrypto.crypto.xml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.X509Data;

public class X509DataImpl extends NodeReader implements X509Data {

    private final String issuerName;
    private final String serialNumber;
    private final String key;

    public X509DataImpl(String issuerName, String serialNumber, String key) {
        this.issuerName = issuerName;
        this.serialNumber = serialNumber;
        this.key = key;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getKey() {
        return key;
    }
}

