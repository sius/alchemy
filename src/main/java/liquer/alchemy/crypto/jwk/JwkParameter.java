package liquer.alchemy.crypto.jwk;

public final class JwkParameter {
	public static final String kty = "kty";
	public static final String use = "use";
	public static final String key_ops = "key_ops";
	public static final String alg = "alg";
	public static final String kid = "kid";
	public static final String x5u = "x5u";
	public static final String x5c = "x5c";
	public static final String x5t = "x5t";
	public static final String x5t_S256 = "x5t#S256";
	/**
	 * key set
	 */
	public static final String keys = "keys";
	/**
	 * key
	 */
	public static final String k = "k";
	/**
	 * public exponent
	 */
	public static final String e = "e";
	/**
	 * 	modulus
 	 */
	public static final String n = "n";
	/**
	 * private exponent
	 */
	public static final String d = "d";
	/**
	 * prime1 (p)
	 */
	public static final String p = "p";
	/**
	 * prime2 (q)
	 */
	public static final String q = "q";

	/**
	 * exponent1 (requires: private exponent, prime1)
	 */
	public static final String dp = "dp";
	/**
	 * exponent2 (requires: private exponent, prime2)
	 */
	public static final String dq = "dq";
	/**
	 * coefficient (requires: prime1, prime2)
	 */
	public static final String qi = "qi";


	public static final String crv = "crv";

	// ECPoint
	public static final String x = "x";
	public static final String y = "y";
}
