package liquer.alchemy.crypto.sign;

import liquer.alchemy.collection.TypedMap;
import liquer.alchemy.collection.Yash;
import liquer.alchemy.crypto.KeyRing;
import liquer.alchemy.crypto.SignatureAlgorithms;
import liquer.alchemy.crypto.jwk.Jwk;
import liquer.alchemy.crypto.jwt.Jwt;
import liquer.alchemy.crypto.jwt.JwtPayload;
import liquer.alchemy.crypto.jwt.JwtVerificationResult;
import liquer.alchemy.crypto.jwt.NumericDate;
import liquer.alchemy.json.Json;
import liquer.alchemy.util.BaseN;
import liquer.alchemy.util.Pedantic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class SimpleURLSigner {

	static final Logger LOG = LogManager.getLogger(SimpleURLSigner.class);
	public static final String KEY_FMT = "%1$06d";
	static final String TOKEN_NAME = "_token";

	static final long DEFAULT_GRANT_TIME = 60000;

	private final SecureRandom rand = new SecureRandom();
	private final KeyRing keyRing;

//	public static void createSpiceProperties(File f, int count) throws IOException {
//		Properties props = new Properties();
//
//		for (int i = 0; i < count; i++)
//			props.setProperty(String.format(KEY_FMT, i), PasswordHashWithNativeDerive.createSecureRandom(PasswordHashWithNativeDerive.SALT_BYTES));
//		try (FileOutputStream fos = new FileOutputStream(f)) {
//			props.store(fos, "Spice" + count);
//		}
//	}

	public SimpleURLSigner(KeyRing keyRing) {
		this(keyRing, 1000);
	}
	public SimpleURLSigner(KeyRing keyRing, int iterations) {
		this.keyRing = keyRing;
    }
	/**
	 * @param url the unsigned URL
	 * @param notBefore notBefore Date
	 * @param expirationTime expirationTime Date
	 * @return the signed URL
	 */
	public String getSignedURL(String url, SignatureAlgorithms algorithm, Date notBefore, Date expirationTime) throws NoSuchAlgorithmException, InvalidKeyException {

		int nextInt = rand.nextInt();
		String queryChar = (url.indexOf('?')> -1 ? "&" : "?");

		JwtPayload payload = new JwtPayload();
		payload.setNotBefore(notBefore);
		payload.setExpirationTime(expirationTime);
		payload.setJwtID(UUID.randomUUID().toString());
		payload.put("random", nextInt);

		Jwt jwt = new Jwt(payload);
		Jwk jwk = keyRing.getJwk(nextInt);
		String signedJwt = jwt.sign(jwk);


		String signString =
			String.format("%1$s%2$s%3$s=%4$s",
				url, queryChar, TOKEN_NAME, signedJwt);

		return String.format("%1$s.%2$s", signString, Jwt.signature(signString, jwk));
	}
	public boolean verify(String requestURI, String queryString) {
		return verify(String.format("%1$s?%2$s", requestURI, queryString));
	}
	public boolean verify(String signedUri) {
		try {
			String queryString = signedUri.substring(signedUri.indexOf('?')+1);
			TypedMap params = new TypedMap(Yash.urlDecode(queryString));
			String token = params.stringValue(TOKEN_NAME);
			if (token == null) {
				return false;
			}
			String[] parts = token.split("\\.", 4);
			if (parts.length < 4) {
				return false;
			}
			JwtPayload payload = new JwtPayload(Json.assign(Map.class, new String(BaseN.base64Decode(parts[1]), StandardCharsets.UTF_8)));
			Integer random = payload.integerValue("random");
			if (random == null) {
				return false;
			}

			Jwk jwk = keyRing.getJwk(random);
			String signedJwt = Pedantic.join(".", parts[0], parts[1], parts[2]);
			JwtVerificationResult result = Jwt.verify(signedJwt, jwk);
			if (!result.isValidSignature()) {
				return false;
			}

			String signString = signedUri.substring(0, signedUri.indexOf(signedJwt) + signedJwt.length());
			if (!parts[3].equals(Jwt.signature(signString, jwk))) {
				return false;
			}

			Date notBefore = payload.getNotBefore();
			Date expirationTime = payload.getExpirationTime();
			Date now = new NumericDate();
			if (notBefore == null || now.before(notBefore)) {
				return false;
			}
            return expirationTime != null && !now.after(expirationTime);
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}
	}
}
