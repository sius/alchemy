package liquer.alchemy.crypto.json.jwt;

public interface JwtCheckResult {

	default boolean isOk() {
		return false;
	}
}
