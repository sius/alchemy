package liquer.alchemy.crypto.json.jwt;

public interface JwtValidationResult {

	default JwtCheckResult issCheck() {
		return null;
	}
	default JwtCheckResult subCheck() {
		return null;
	}
	default JwtCheckResult audCheck() {
		return null;
	}
	default JwtCheckResult expCheck() {
		return null;
	}
	default JwtCheckResult nbfCheck() {
		return null;
	}
	default JwtCheckResult jtiCheck() {
		return null;
	}

	default JwtCheckResult check(JwtParameter param) {
		switch(param) {
			case iss: return issCheck();
			case sub: return subCheck();
			case aud: return audCheck();
			case exp: return expCheck();
			case nbf: return nbfCheck();
			case jti: return jtiCheck();
			default: return check(param.toString());
		}
	}
	default JwtCheckResult check(String param) {
		try {
			check(JwtParameter.valueOf(param));
		} catch (IllegalArgumentException ignore) {

		}
		return null;
	}

	default boolean isValidSignature() { return false; }

	default boolean isValidToken() { return false; }

}
