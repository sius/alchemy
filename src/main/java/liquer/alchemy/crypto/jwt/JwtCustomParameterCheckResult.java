package liquer.alchemy.crypto.jwt;

public class JwtCustomParameterCheckResult implements JwtCheckResult {

	private final String parameter;

	JwtCustomParameterCheckResult(String parameter) {
		this.parameter = parameter;
	}
}
