package liquer.alchemy.xmlcrypto.crypto;

import liquer.alchemy.xmlcrypto.support.BaseN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.function.Function;

public final class CryptoSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoSupport.class);

    private static final String PROVIDER;

    static {
        Security.setProperty("crypto.policy", "unlimited");
        if (Security.getProvider("BC") != null) {
            PROVIDER = "BC";
        } else {
            PROVIDER = "SUN";
        }
    }

    private CryptoSupport() { }

    /**
     *
     * @param value
     * @param algorithm
     * @param key
     * @param passphrase
     * @param charset
     * @return the signature data or null
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     */
    public static byte[] sign(
        String value,
        String algorithm,
        byte[] key,
        char[] passphrase,
        Charset charset) {

        final Charset finalCharset = charset == null
            ? StandardCharsets.UTF_8
            : charset;

        try {
            if (algorithm.startsWith("Hmac")) {
                Mac mac = getMac(algorithm, key);
                return mac.doFinal(value.getBytes(finalCharset));
            } else {
                Signature sig = getPrivateSignature(algorithm, key, passphrase);
                sig.update(value.getBytes(finalCharset));
                return sig.sign();
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new CryptoException(e);
        }
    }

    /**
     *
     * @param value
     * @param algorithm
     * @param key
     * @param passphrase
     * @param charset
     * @param encoder
     * @return
     */
    public static String signHex(
        String value,
        String algorithm,
        byte[] key,
        char[] passphrase,
        Charset charset,
        Function<byte[], String> encoder) {

        final Function<byte[], String> enc = encoder == null
            ? BaseN::base64UrlEncode
            : encoder;

        return enc.apply(sign(value, algorithm, key, passphrase, charset));
    }

    /*
     * @param data byte array
     * @return the null/zero terminated array
     */
    private static byte[] zeroTerminate(byte[]  data) {

        if (data == null) {
            return new byte[] { 0 };
        }

        byte[] copy = new byte[data.length+1];
        System.arraycopy(data, 0, copy, 0, data.length);
        copy[data.length] = 0;
        return copy;
    }

    /**
     *
     * @param value
     * @param algorithm
     * @param nullTerminated
     * @param charset
     * @return the digest data
     */
    public static byte[] hash(
        String value,
        String algorithm,
        boolean nullTerminated,
        Charset charset) {

        if (value == null) {
            return new byte[0];
        }

        byte[] data = value.getBytes(charset == null
            ? StandardCharsets.UTF_8
            : charset);

        if (nullTerminated) {
            data = zeroTerminate(data);
        }

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm, PROVIDER);
            return md.digest(data);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            LOG.error(e.getMessage(), e);
            throw new CryptoException(e);
        }

    }

    /**
     *
     * @param value
     * @param algorithm
     * @param nullTerminated
     * @param charset
     * @param encoder
     * @return
     */
    public static String hashHex(
        String value,
        String algorithm,
        boolean nullTerminated,
        Charset charset,
        Function<byte[], String> encoder) {

        final Function<byte[], String> enc = encoder == null
            ? BaseN::base64UfsEncode
            : encoder;

        return enc.apply(hash(value, algorithm, nullTerminated, charset));
    }

    /**
     *
     * @param algorithm
     * @param key
     * @return the Message Authentication Code instance
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Mac getMac(String algorithm, byte[] key)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac;
    }

    /**
     * Create a signature instance for verification
     * @param algorithm the signature algorithm
     * @param publicKey the public key
     * @return the Signature instance initialized for signature verification
     */
    public static Signature getPublicSignature(String algorithm, byte[] publicKey) {
        try {
            Signature publicSignature = Signature.getInstance(algorithm);
            PublicKey pk = readPublicKeyFromX509Cert(publicKey);
            publicSignature.initVerify(pk);
            return publicSignature;
        } catch (NoSuchAlgorithmException | CertificateException | InvalidKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new CryptoException(e);
        }

    }

    /**
     /**
     * Create a signature instance for signing
     * @param algorithm the signature algorithm
     * @param privateKey the private key
     * @return the Signature instance initialized for signing
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static Signature getPrivateSignature(String algorithm, byte[] privateKey, char[] passphrase)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Signature privateSignature = Signature.getInstance(algorithm);
        PrivateKey pk = new PKCS8Data(privateKey, passphrase).getPrivateKey();
        privateSignature.initSign(pk);
        return privateSignature;
    }
    /**
     * Read X.509 Certificate.
     *
     * @param cert
     *            the cert as a byte array.

     * @return extracted public key
     * @throws CertificateException
     */
    public static PublicKey readPublicKeyFromX509Cert(final byte[] cert) throws CertificateException {
        return readX509Certificate(cert).getPublicKey();
    }

    /**
     *
     * @param cert
     * @return
     * @throws CertificateException
     */
    public static X509Certificate readX509Certificate(final byte[] cert) throws CertificateException {
        byte[] data = cert.clone();
        if (((char) data[0]) != '-') {
            StringBuilder b = new StringBuilder();
            b.append(Identifier.BEGIN_CERT)
                .append('\n')
                .append(new String(data, StandardCharsets.UTF_8))
                .append('\n')
                .append(Identifier.END_CERT);
            data = b.toString().getBytes(StandardCharsets.UTF_8);
        }

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        return (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(data));
    }

    /**
     * @param value
     * @param signature
     * @param algorithm
     * @param key
     * @param charset
     * @return result
     */
    public static boolean verify (
        String value,
        byte[] signature,
        String algorithm,
        byte[] key,
        Charset charset) {

        if (signature == null || key == null) {
            return false;
        }

        final Charset finalCharset = charset == null
            ? StandardCharsets.UTF_8
            : charset;

        try {
            if (algorithm.startsWith("Hmac")) {
                final Mac mac = getMac(algorithm, key);
                final int digestByteLen = mac.getMacLength() / 8;
                final byte[] computed = mac.doFinal(value.getBytes(finalCharset));
                return compareDigests(digestByteLen, computed, signature);
            } else {
                Signature sig = getPublicSignature(algorithm, key);
                sig.update(value.getBytes(finalCharset));
                return sig.verify(signature);
            }
        } catch ( NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            LOG.error(e.getMessage(), e);
            throw new CryptoException(e);
        }
    }

    private static boolean compareDigests(int digestByteLen, byte[] computed, byte[] signature) {

        if (digestByteLen != computed.length || digestByteLen != signature.length) {
            return false;
        }

        for (int i = 0; i < computed.length; i++) {
            if ((computed[i] != signature[i])) {
                return false;
            }
        }
        return true;
    }
}
