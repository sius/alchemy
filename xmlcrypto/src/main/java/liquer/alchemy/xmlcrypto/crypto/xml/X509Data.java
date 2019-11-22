package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.CryptoSupport;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface X509Data {

    String getIssuerName();

    String getSerialNumber();

    String getKey();

    default X509Certificate getCertificate() throws CertificateException {
        final String key = getKey();
        return (key != null)
            ? CryptoSupport.readX509Certificate(key.getBytes(StandardCharsets.UTF_8))
            : null;
    }
}
