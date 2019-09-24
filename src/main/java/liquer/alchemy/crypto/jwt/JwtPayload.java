package liquer.alchemy.crypto.jwt;

import liquer.alchemy.collection.TypedMap;

import java.util.Date;
import java.util.Map;

import static liquer.alchemy.crypto.jwt.JwtParameter.*;

public class JwtPayload extends TypedMap {

	public JwtPayload() { super(); }
	public JwtPayload(Map<String, Object> map) { super(map); }

	/**
	 * Issuer - Identifies principal that issued the JWT.
	 */
	public Object getIssuer() {
		return get(iss);
	}

	public void setIssuer(String issuer) {
		putWithObjectKey(iss, issuer);
	}

	/**
	 * Subject - Identifies the subject of the JWT.
	 */
	public Object getSubject() {
		return get(sub);
	}

	public void setSubject(String subject) {
		putWithObjectKey(sub, subject);
	}

	/**
	 * Audience - Identifies the recipients that the JWT is intended for. Each principal intended to process the JWT must identify itself with a value in the audience claim. If the principal processing the claim does not identify itself with a value in the aud claim when this claim is present, then the JWT must be rejected.
	 */
	public Object getAudience() {
		return get(aud);
	}

	public void setAudience(String audience) {
		putWithObjectKey(aud, audience);
	}

	/**
	 * Expiration Time - Identifies the expiration time on and after which the JWT must not be accepted for processing. The value must be a NumericDate[10]: either an integer or decimal, representing seconds past 1970-01-01 00:00:00Z.
	 * @return expirationTime Date
	 */
	public Date getExpirationTime() {
		Long seconds = longValue(exp.toString());
		if (seconds != null) {
			return new NumericDate(seconds);
		}
		return null;
	}

	/**
	 * Set Expiration Time - Identifies the expiration time on and after which the JWT must not be accepted for processing. The value must be a NumericDate[10]: either an integer or decimal, representing seconds past 1970-01-01 00:00:00Z.
	 * @param expirationTime Date
	 */
	public void setExpirationTime(Date expirationTime) {
		if (expirationTime != null) {
			putWithObjectKey(exp, new NumericDate(expirationTime).getSecondsAsLong());
		}

	}

	/**
	 * Not Before - Identifies the time on which the JWT will start to be accepted for processing. The value must be a NumericDate.
	 * @return notBefore Date
	 */
	public Date getNotBefore() {
		Long seconds = longValue(nbf.toString());
		if (seconds != null) {
			return new NumericDate(seconds);
		}
		return null;
	}
	/**
	 * Set Not Before - Identifies the time on which the JWT will start to be accepted for processing. The value must be a NumericDate.
	 * @param notBefore Date
	 */
	public void setNotBefore(Date notBefore) {
		if (notBefore != null) {
			putWithObjectKey(nbf, new NumericDate(notBefore).getSecondsAsLong());
		}
	}


	/**
	 * Issued at - Identifies the time at which the JWT was issued. The value must be a NumericDate.
	 * @return issuedAt Date
	 */
	public Date getIssuedAt() {
		Long seconds = longValue(iat.toString());
		if (seconds != null) {
			return new NumericDate(seconds);
		}
		return null;
	}

	/**
	 * Issued at - Identifies the time at which the JWT was issued. The value must be a NumericDate.
	 * @param issuedAt Date
	 */
	void setIssuedAt(Date issuedAt) {
		if (issuedAt != null) {
			putWithObjectKey(iat, new NumericDate(issuedAt).getSecondsAsLong());
		}
	}


	/**
	 * JWT ID - Case sensitive unique identifier of the token even among different issuers.
	 */
	public String getJwtID() {
		return stringValue(jti.toString());
	}

	/**
	 * Set JWT ID - Case sensitive unique identifier of the token even among different issuers.
	 * @param jwtID the unique ID
	 */
	public void setJwtID(String jwtID) {
		putWithObjectKey(jti, jwtID);
	}
}
