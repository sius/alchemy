package liquer.alchemy.xmlcrypto.crypto;

import liquer.alchemy.xmlcrypto.support.BaseN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * PKCS8_UNENCRYPTED: PRIVATE KEY
 * PKCS8_ENCRYPTED: ENCRYPTED PRIVATE KEY
 * OPENSSL_RSA: RSA PRIVATE KEY
 * OPENSSL_DSA: DSA PRIVATE KEY
 */
public final class PKCS8Data {

    private static final Logger LOG = LoggerFactory.getLogger(PKCS8Data.class);

    private static final String MARKUP = "-----";
    private static final String MARKUP_BEGIN = "-----BEGIN ";
    private static final String MARKUP_END = "-----END ";

    private static final String SPLIT_REGEX = "(\r?\n|\r)";


    private final Key key;
    private final String type;
    private final boolean isPrivate;
    private final boolean isPEMEncoded;
    private final boolean encrypted;

    /**
     * Read the PKCS#8 Data (ignoring the key structure) and try to generate appropriate key
     *
     * Better use PCKSKey from see not-going-to-be-common-ssl
     * (https://github.com/narupley/not-going-to-be-commons-ssl)
     * or PEMReader from BouncyCastle to decrypt encrypted private keys
     *
     * https://polarssl.org/kb/cryptography/asn1-key-structures-in-der-and-pem
     * @param pkcs8Data May be DER or PEM encoded. Cannot decrypt encrypted keys.
     * @param passphrase May be encrypted. Not null if private key; encrypted passphrase length >= 4
     * @return new instance
     */
    public PKCS8Data(byte[] pkcs8Data, char[] passphrase) {
        try {
            isPEMEncoded = isPEMEncoded(pkcs8Data);
            if (isPEMEncoded) {

                final List<String> lines = new LinkedList<>(Arrays.asList(
                    new String(pkcs8Data, StandardCharsets.UTF_8).split(SPLIT_REGEX)));
                final String header = lines.remove(0).trim();

                if (!isPKCS8DataHeader(header)) {
                    throw new IllegalArgumentException("Unexpected PEM header '" + header + "'");
                }

                type = header
                    .replace(MARKUP_BEGIN, "")
                    .replace(MARKUP, "");

                final String footer = lines.remove(lines.size() - 1).trim();

                if (!footer.equals(MARKUP_END + type + MARKUP)) {
                    throw new IllegalArgumentException("Unexpected PEM footer '" + footer + "'");
                }

                encrypted = type.contains("ENCRYPTED");
                isPrivate = type.contains("PRIVATE");

                final String encoded = String.join("", lines);
                key = generatePrivateKeyFromPKCS8(
                    BaseN.base64Decode(encoded), encrypted, passphrase);

            } else {
                type = null;
                isPrivate = passphrase != null;
                encrypted = (passphrase.length > 0);
                key = generatePrivateKeyFromPKCS8(pkcs8Data, encrypted, passphrase);
            }

        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static boolean isPEMEncoded(byte[] data) {
        return data != null ? (((char) data[0]) == '-') : false;
    }
    private static boolean isPKCS8DataHeader(String header) {
        return header != null && header.startsWith(MARKUP_BEGIN) && header.endsWith(MARKUP);
    }

    public PrivateKey getPrivateKey() {
        return (PrivateKey) (isPrivateKey() ? getKey(): null);
    }

    public PublicKey getPublicKey() {
        return (PublicKey) (isPublicKey() ? getKey(): null);
    }

    public Key getKey() {
        return key;
    }

    public String getKeyType() {
        return type;
    }

    public boolean isPrivateKey() {
        return isPrivate;
    }

    public boolean isPublicKey() {
        return !isPrivateKey();
    }

    /**
     * Read a PKCS#8 encoded public key and generate PublicKey
     * Must be DER encoded.
     *
     * @param pkcs8Data the public key data as a byte array.
     * @return parsed public key.
     */
    public static PublicKey generatePublicKeyFromPKCS8(byte[] pkcs8Data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(pkcs8Data);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            return KeyFactory.getInstance("DSA").generatePublic(spec);
        }
    }

    /**
     * Read a PKCS#8 encoded private key and generate PrivateKey.
     * Must be DER encoded.
     * May be encrypted.
     *
     * @param pkcs8Data the private key data as a byte array.
     * @param encrypted
     * @param passphrase
     *
     * @return parsed private key.
     */
    private static PrivateKey generatePrivateKeyFromPKCS8(byte[] pkcs8Data, boolean encrypted, char[] passphrase) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
        if (encrypted) {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase);
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(pkcs8Data);
            // Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
            Key secret = skf.generateSecret(pbeKeySpec);
            PKCS8EncodedKeySpec keySpec = encryptedPrivateKeyInfo.getKeySpec(secret);
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                LOG.warn(e.getMessage(), e);
                return KeyFactory.getInstance("DSA").generatePrivate(keySpec);
            }
        } else {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8Data);
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                LOG.warn(e.getMessage(), e);
                return KeyFactory.getInstance("DSA").generatePrivate(spec);
            }
        }
    }
}
