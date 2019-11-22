package liquer.alchemy.xmlcrypto.crypto.alg;

import liquer.alchemy.xmlcrypto.crypto.CryptoException;
import liquer.alchemy.xmlcrypto.crypto.CryptoSupport;
import liquer.alchemy.xmlcrypto.crypto.opt.EncoderOptions;
import liquer.alchemy.xmlcrypto.support.BaseN;

import java.nio.charset.StandardCharsets;

public interface SignatureAlgorithm extends Algorithm {

    int getBlockSize();

    int getOutputLength();

    int getTruncatedLength();

    int getKeyLength();

    boolean isKeyPair();

    String getKeyPairGeneratorAlgorithm();

    /**
     * Sign the given string using the given key
     *
     * @param givenString The String to sign
     * @param signingKey The signing key
     * @param passphrase The passphrase to decrypt private key,
     *                   an empty key signifies an unencrypted key
     * @param options The encoder options
     * @return the signed givenString
     * @throws CryptoException
     */
    default String sign(
            String givenString,
            String signingKey,
            char[] passphrase,
            EncoderOptions options) {

        final EncoderOptions encoderOptions = options == null
            ? new EncoderOptions()
            : options;

        return CryptoSupport.signHex(
            givenString,
            getName(),
            signingKey.getBytes(StandardCharsets.UTF_8),
            passphrase,
            encoderOptions.getCharset(),
            encoderOptions.getEncoder());

    }

    /**
     * Verify the given signature of the given string using key
     *
     * @param givenString The given String to verify
     * @param key The secret or public key
     * @param givenSignature The given signature base64 String
     * @param options The encoder options
     * @return true if the verification succeeds, else false
     */
    default boolean verify(
            String givenString,
            String key,
            String givenSignature,
            EncoderOptions options) {

        final EncoderOptions encoderOptions = options == null
            ? new EncoderOptions()
            : options;

        return CryptoSupport.verify(
            givenString,
            BaseN.base64Decode(givenSignature),
            getName(),
            key.getBytes(StandardCharsets.UTF_8),
            encoderOptions.getCharset());
    }
}
