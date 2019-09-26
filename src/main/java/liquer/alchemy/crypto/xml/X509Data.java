package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.CryptoLimericks;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface X509Data {

    String getIssuerName();

    String getSerialNumber();

    String getKey();

    default X509Certificate getCertificate() throws CertificateException {
        final String key = getKey();
        return (key != null)
            ? CryptoLimericks.readX509Certificate(key.getBytes())
            : null;
    }
}
