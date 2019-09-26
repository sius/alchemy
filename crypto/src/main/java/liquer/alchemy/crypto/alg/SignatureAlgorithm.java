package liquer.alchemy.crypto.alg;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.ext.EncoderOptions;
import liquer.alchemy.alembic.BaseN;

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
	 * @param options The encoder options
	 * @return the signed givenString
     */
    default String sign(String givenString, String signingKey, EncoderOptions options) {
    	final EncoderOptions finalOpts = options == null ? new EncoderOptions() : options;
		return CryptoLimericks.sign(givenString, getName(), signingKey.getBytes(StandardCharsets.UTF_8), finalOpts.getCharset(), finalOpts.getEncoder());
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
    default boolean verify(String givenString, String key, String givenSignature, EncoderOptions options) {
		final EncoderOptions finalOpts = options == null ? new EncoderOptions() : options;
		return CryptoLimericks.verify(givenString, BaseN.base64Decode(givenSignature), getName(), key.getBytes(StandardCharsets.UTF_8), finalOpts.getCharset());
	}
}
