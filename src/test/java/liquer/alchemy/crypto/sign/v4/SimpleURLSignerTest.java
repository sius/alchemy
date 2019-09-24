package liquer.alchemy.crypto.sign.v4;


import liquer.alchemy.crypto.KeyRing;
import liquer.alchemy.crypto.SignatureAlgorithms;
import liquer.alchemy.crypto.jwt.NumericDate;
import liquer.alchemy.crypto.sign.SimpleURLSigner;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.TestCase.assertTrue;

public class SimpleURLSignerTest {

	@Test
	public void getSignedURL_And_Verify() throws NoSuchAlgorithmException, InvalidKeyException {
		KeyRing keyRing = new KeyRing(SignatureAlgorithms.HS256, 10);
		NumericDate notBefore = new NumericDate();
		SimpleURLSigner signer = new SimpleURLSigner(keyRing);
		String signed = signer.getSignedURL("http://unsigned:8080/doc/1?first=test&second=1", SignatureAlgorithms.HS256, notBefore, notBefore.addSeconds(300));
		assertTrue(signer.verify(signed));
	}
}



