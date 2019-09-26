package liquer.alchemy.support;

import liquer.alchemy.crypto.KeyRing;
import liquer.alchemy.crypto.Signature;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;


public class StringSupportTest {

	@Test
	public void hexEncode() throws NoSuchAlgorithmException {
		new KeyRing(Signature.HS256, 1);
	}
}
