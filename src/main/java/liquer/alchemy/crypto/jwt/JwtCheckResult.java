package liquer.alchemy.crypto.jwt;

public interface JwtCheckResult {

	default boolean isOk() {
		return false;
	}
}
