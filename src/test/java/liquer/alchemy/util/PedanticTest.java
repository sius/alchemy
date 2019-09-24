package liquer.alchemy.util;

import liquer.alchemy.crypto.KeyRing;
import liquer.alchemy.crypto.SignatureAlgorithms;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;


public class PedanticTest {

	@Test
	public void hexEncode() throws NoSuchAlgorithmException {
		new KeyRing(SignatureAlgorithms.HS256, 1);
	}
}
