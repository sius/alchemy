package liquer.alchemy.crypto.json.jwt;

public class JwtParameterCheckResult implements JwtCheckResult {

	private final JwtParameter parameter;

	JwtParameterCheckResult(JwtParameter parameter) {
		this.parameter = parameter;
	}
}
