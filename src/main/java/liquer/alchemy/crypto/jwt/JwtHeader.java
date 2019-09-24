package liquer.alchemy.crypto.jwt;

import liquer.alchemy.collection.TypedMap;
import liquer.alchemy.crypto.SignatureAlgorithms;

import java.util.Map;

import static liquer.alchemy.crypto.jwt.JwtParameter.*;

public class JwtHeader extends TypedMap {

	public JwtHeader(Map<String, Object> map) {
		super(map);
	}

	public JwtHeader() {
		this(SignatureAlgorithms.HS256, "JWT");
	}

	public JwtHeader(SignatureAlgorithms SignatureAlgorithm, String tokenType) {
		super();
		this.putWithObjectKey(typ, tokenType);
		this.putWithObjectKey(alg, SignatureAlgorithm);
	}

	/**
	 * Message authentication code algorithm - The issuer can freely set an algorithm to verify the signature on the token. However, some supported algorithms are insecure[1
	 *
	 */
	public SignatureAlgorithms getAlgorithm() {
		return enumValue(SignatureAlgorithms.class, alg.toString(), null);
	}

	void setAlgorithm(SignatureAlgorithms SignatureAlgorithm) { this.put(alg.toString(), SignatureAlgorithm); }

	/**
	 * Token type - If present, it is recommended to set this to JWT.
	 */
	public String getTokenType() {
		return stringValue(typ.toString(), "JWT");
	}

	/**
	 * Content type	- If nested signing or encryption is employed, it is recommended to set this to JWT, otherwise omit this field[1].
	 */
	public Object getContentType() {
		return get(cty);
	}

	void setContentType(String contentType) { this.putWithObjectKey(cty, contentType); }
}
