package liquer.alchemy.crypto.jwt;

import liquer.alchemy.collection.TypedMap;
import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.SignatureAlgorithms;
import liquer.alchemy.crypto.jwk.Jwk;
import liquer.alchemy.json.Json;
import liquer.alchemy.util.BaseN;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public final class Jwt extends TypedMap {

	private static class VerificationResult implements JwtVerificationResult {

		private final boolean validSignature;

		private VerificationResult(String encodedJwt, SignatureAlgorithms algorithm, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
			final String[] parts = encodedJwt.split("\\.");
			if (parts.length == 3) {
				final String signString = String.format("%1$s.%2$s", parts[0], parts[1]);
				final String signature = Jwt.signature(signString, algorithm, secret);
				final String jwt = String.format("%1$s.%2$s", signString, signature);
				validSignature = jwt.equals(encodedJwt);
			} else {
				validSignature = false;
			}
		}
		@Override
		public boolean isValidSignature() {
			return validSignature;
		}
	}
	public Jwt(Map<String, Object> map) { super(map); }
	public Jwt(JwtPayload payload) {
		this(new JwtHeader(), payload);
	}
	public Jwt(JwtHeader header, JwtPayload payload) {
		super();
		putWithObjectKey(JwtParameter.header, header);
		putWithObjectKey(JwtParameter.payload, payload);
	}

	public JwtHeader getHeader() {
		Object ret =  this.get(JwtParameter.header);
		return (ret instanceof JwtHeader) ? (JwtHeader)ret : null;
	}

	public JwtPayload getPayload() {
		Object ret = get(JwtParameter.payload);
		return (ret instanceof JwtPayload) ? (JwtPayload)ret : null;
	}

	public String sign(Jwk key) throws InvalidKeyException, NoSuchAlgorithmException {
		return sign(key.getAlgorithm(), key.getKey().getEncoded());
	}

	public String sign(SignatureAlgorithms algorithm, byte[] secret) throws InvalidKeyException, NoSuchAlgorithmException {
		this.getHeader().setAlgorithm(algorithm);
		this.getPayload().setIssuedAt(new NumericDate());
		return buildSignedJwt(signString(), algorithm, secret);
	}

	private String signString() {
		final String h = BaseN.base64UrlEncode(Json.stringify(this.getHeader()).getBytes(StandardCharsets.UTF_8));
		final String p = BaseN.base64UrlEncode(Json.stringify(this.getPayload()).getBytes(StandardCharsets.UTF_8));
		return String.format("%1$s.%2$s", h, p);
	}

	public static String signature(String signString, Jwk key) {
		return BaseN.base64UrlEncode(CryptoLimericks.sign(signString, key.getAlgorithm(), key.getKey().getEncoded()));
	}
	public static String signature(String signString, SignatureAlgorithms algorithm, byte[] secret) {
		return BaseN.base64UrlEncode(CryptoLimericks.sign(signString, algorithm, secret));
	}

	private static String buildSignedJwt(String signString, SignatureAlgorithms algorithm, byte[] secret) {
		return String.format("%1$s.%2$s", signString, signature(signString, algorithm, secret));
	}

	public static JwtVerificationResult verify(String encodedJwt, Jwk key) throws InvalidKeyException, NoSuchAlgorithmException {
		return verify(encodedJwt, key.getAlgorithm(), key.getKey().getEncoded());
	}

	public static JwtVerificationResult verify(String encodedJwt, SignatureAlgorithms algorithm, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
		return new VerificationResult(encodedJwt, algorithm, secret);
	}

}
