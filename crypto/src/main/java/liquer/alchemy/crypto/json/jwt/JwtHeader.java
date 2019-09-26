package liquer.alchemy.crypto.json.jwt;

import liquer.alchemy.athanor.TypedMap;
import liquer.alchemy.crypto.Signature;

import java.util.Map;

import static liquer.alchemy.crypto.json.jwt.JwtParameter.*;

public class JwtHeader extends TypedMap {

	public JwtHeader(Map<String, Object> map) {
		super(map);
	}

	public JwtHeader() {
		this(Signature.HS256, "JWT");
	}

	public JwtHeader(Signature SignatureAlgorithm, String tokenType) {
		super();
		this.putWithObjectKey(typ, tokenType);
		this.putWithObjectKey(alg, SignatureAlgorithm);
	}

	/**
	 * Message authentication code algorithm - The issuer can freely set an algorithm to verify the signature on the token. However, some supported algorithms are insecure[1
	 *
	 */
	public Signature getAlgorithm() {
		return enumValue(Signature.class, alg.toString(), null);
	}

	void setAlgorithm(Signature SignatureAlgorithm) { this.put(alg.toString(), SignatureAlgorithm); }

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
