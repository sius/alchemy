package liquer.alchemy.xmlcrypto.crypto;

import liquer.alchemy.xmlcrypto.support.BaseN;

import javax.crypto.Cipher;
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

public final class PKCS8Data {


    public static final String PKCS8_UNENCRYPTED = "PRIVATE KEY";
    public static final String PKCS8_ENCRYPTED = "ENCRYPTED PRIVATE KEY";
    public static final String OPENSSL_RSA = "RSA PRIVATE KEY";
    public static final String OPENSSL_DSA = "DSA PRIVATE KEY";

    private final Key key;
    private String type;
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

        if (pkcs8Data != null) {
            isPEMEncoded = (((char)pkcs8Data[0]) == '-');

            if (isPEMEncoded) {
                List<String> lines = new LinkedList<>(Arrays.asList(
                    new String(pkcs8Data, StandardCharsets.UTF_8).split("(\r\n|\n|\r)")));
                String header = lines.remove(0).trim();
                String footer = lines.remove(lines.size() - 1).trim();

                if (header.startsWith("-----BEGIN ") && header.endsWith("-----")) {
                    type = header;
                    type = type.replace("-----BEGIN ", "");
                    type = type.replace("-----", "");

                    encrypted = type.contains("ENCRYPTED");
                    isPrivate = type.contains("PRIVATE");

                    if (footer.equals("-----END " + type + "-----")) {
                        try {
                            String encoded = String.join("", lines);
                            final byte[] pkcs8 = BaseN.base64Decode(encoded);
                            key = isPrivate
                                ? generatePrivateKeyFromPKCS8(pkcs8, encrypted, passphrase)
                                : generatePublicKeyFromPKCS8(pkcs8);
                        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | IOException | NoSuchPaddingException e) {
                            throw new IllegalArgumentException(e);
                        }
                    } else {
                        throw new IllegalArgumentException("Unexpected PEM footer '" + footer + "'");
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected PEM header '" + header + "'");
                }
            } else {
                if (passphrase != null) {
                    try {
                        if (passphrase.length > 0 && passphrase.length < 4) {
                            throw new IllegalArgumentException("Passphrase length for an unencrypted key must be 0, " +
                                "for an encrypted key the passphrase length must be >= 4");
                        }
                        isPrivate = true;
                        encrypted = (passphrase.length > 0);
                        key = generatePrivateKeyFromPKCS8(pkcs8Data, encrypted, passphrase);
                    } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException e) {
                        throw new IllegalArgumentException(e.getMessage(), e);
                    }
                } else {
                    isPrivate = false;
                    encrypted = false;
                    key = generatePublicKeyFromPKCS8(pkcs8Data);
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected PKCS#8 Data");
        }
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
    public static PublicKey generatePublicKeyFromPKCS8(byte[] pkcs8Data) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(pkcs8Data);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            try {
                return KeyFactory.getInstance("DSA").generatePublic(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e2) {
                throw new RuntimeException(e2);
            }
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
    public static PrivateKey generatePrivateKeyFromPKCS8(byte[] pkcs8Data, boolean encrypted, char[] passphrase) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
        if (encrypted) {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase);
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(pkcs8Data);
            Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
            Key secret = skf.generateSecret(pbeKeySpec);
            PKCS8EncodedKeySpec keySpec = encryptedPrivateKeyInfo.getKeySpec(secret);
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                try {
                    return KeyFactory.getInstance("DSA").generatePrivate(keySpec);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e2) {
                    throw new RuntimeException(e2);
                }
            }
        } else {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8Data);
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                try {
                    return KeyFactory.getInstance("DSA").generatePrivate(spec);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e2) {
                    throw new RuntimeException(e2);
                }
            }
        }
    }
}
