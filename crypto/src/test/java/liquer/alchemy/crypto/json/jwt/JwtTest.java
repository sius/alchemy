package liquer.alchemy.crypto.json.jwt;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.KeyRing;
import liquer.alchemy.crypto.Signature;
import liquer.alchemy.crypto.json.jwk.Jwk;
import liquer.alchemy.athanor.json.Json;
import liquer.alchemy.alembic.BaseN;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTest {

	@Test
	void signAndVerifyHS256() throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException {
		signAndVerify(Signature.HS256);
	}

	@Test
	void signAndVerifyHS384() throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException {
		signAndVerify(Signature.HS384);
	}

	@Test
	void signAndVerifyHS512() throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException {
		signAndVerify(Signature.HS512);
	}

	@Test
	void signAndVerifyRS256() throws NoSuchAlgorithmException, FileNotFoundException {
		KeyRing keyRing = new KeyRing(Signature.RS256, 2048, 2);
		String alchemy_keyring = System.getProperty("user.home") + "/.alchemy_keyring";
		Json.writeTo(keyRing, new FileOutputStream(alchemy_keyring), 2);
	}

	private void signAndVerify(Signature algorithm) throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException {
		KeyRing keyRing = new KeyRing(algorithm, 1);
		int random = keyRing.nextInt();

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Date notBefore = c.getTime();

		c.add(Calendar.MINUTE, 15);
		Date expirationTime =  c.getTime();

		JwtPayload payload = new JwtPayload();
		payload.setNotBefore(notBefore);
		payload.setExpirationTime(expirationTime);
		payload.setJwtID(UUID.randomUUID().toString());
		payload.put("random", String.valueOf(random));

		Jwt jwt = new Jwt(payload);

		String alchemy_keyring = System.getProperty("user.home") + "/.alchemy_keyring";

		Json.writeTo(keyRing, new FileOutputStream(alchemy_keyring), 2);

		Jwk jwk = keyRing.getJwk(random);


		String encodedJwt = jwt.sign(jwk);
		System.out.println(encodedJwt);

		JwtValidationResult result = Jwt.verify(encodedJwt, jwk);

		assertTrue(result.isValidSignature());
	}

	/**
	 * https://tools.ietf.org/html/rfc7515#page-8
	 */
	@Test
	public void jwtHeader() {
		String header = "{\"typ\":\"JWT\",\r\n \"alg\":\"HS256\"}";

		String expected = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9";

		assertEquals(expected, BaseN.base64UrlEncode(header.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	public void jwtPayload() {
		String payload = "{\"iss\":\"joe\",\r\n " +
			"\"exp\":1300819380,\r\n " +
			"\"http://example.com/is_root\":true}";

		String expected = "eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ";

		assertEquals(expected, BaseN.base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * {"kty":"oct",
	 *       "k":"AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"
	 *      }
	 */
	@Test
	public void signature() {

		String expected = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9" +
			"." +
			"eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ" +
			"." +
			"dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";


		String header = "{\"typ\":\"JWT\",\r\n \"alg\":\"HS256\"}";
		String payload = "{\"iss\":\"joe\",\r\n " +
			"\"exp\":1300819380,\r\n " +
			"\"http://example.com/is_root\":true}";

		String h = BaseN.base64UrlEncode(header.getBytes());
		String p = BaseN.base64UrlEncode(payload.getBytes());
		final String signString = String.format("%1$s.%2$s", h, p);


		SecretKey secretKey = new SecretKeySpec(BaseN.base64Decode(
			"AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"), Signature.HS256.getName());

		Jwk jwk = new Jwk(Signature.HS256, secretKey);

		final String signature = BaseN.base64UrlEncode(CryptoLimericks.sign(signString, Signature.HS256, jwk.getKey().getEncoded()));
		String actual = String.format("%1$s.%2$s.%3$s", h, p, signature);
		System.out.println(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void getNamedCurves() {
		String[] curves = Security.getProvider("SunEC")
			.getProperty("AlgorithmParameters.EC SupportedCurves").split("\\|");
		for (String curve : curves) {
			System.out.println(curve.substring(1, curve.indexOf(",")));
		}
	}

}
