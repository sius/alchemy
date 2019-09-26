package liquer.alchemy.support;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class BaseNTest {

	@Test
	public void b64SpaceDrain() {
	}

	@Test
	public void b64Wrap() {
	}

	@Test
	public void wrap() {
	}

	@Test
	public void unwrap() {
	}

	@Test
	public void base64Encode() {
		assertEquals("", BaseN.base64Encode("".getBytes()));
		assertEquals("Zg==", BaseN.base64Encode("f".getBytes()));
		assertEquals("Zm8=", BaseN.base64Encode("fo".getBytes()));
		assertEquals("Zm9v", BaseN.base64Encode("foo".getBytes()));
		assertEquals("Zm9vYg==", BaseN.base64Encode("foob".getBytes()));
		assertEquals("Zm9vYmE=", BaseN.base64Encode("fooba".getBytes()));
		assertEquals("Zm9vYmFy", BaseN.base64Encode("foobar".getBytes()));
	}

	@Test
	public void base64UfsEncode() {
		assertEquals("", BaseN.base64Encode("".getBytes()));
		assertEquals("Zg==", BaseN.base64Encode("f".getBytes()));
		assertEquals("Zm8=", BaseN.base64Encode("fo".getBytes()));
		assertEquals("Zm9v", BaseN.base64Encode("foo".getBytes()));
		assertEquals("Zm9vYg==", BaseN.base64Encode("foob".getBytes()));
		assertEquals("Zm9vYmE=", BaseN.base64Encode("fooba".getBytes()));
		assertEquals("Zm9vYmFy", BaseN.base64Encode("foobar".getBytes()));
	}

	@Test
	public void base64UrlEncode() {
		assertEquals("", BaseN.base64UrlEncode("".getBytes()));
		assertEquals("Zg", BaseN.base64UrlEncode("f".getBytes()));
		assertEquals("Zm8", BaseN.base64UrlEncode("fo".getBytes()));
		assertEquals("Zm9v", BaseN.base64UrlEncode("foo".getBytes()));
		assertEquals("Zm9vYg", BaseN.base64UrlEncode("foob".getBytes()));
		assertEquals("Zm9vYmE", BaseN.base64UrlEncode("fooba".getBytes()));
		assertEquals("Zm9vYmFy", BaseN.base64UrlEncode("foobar".getBytes()));
	}

	@Test
	public void base64Decode() {
		String[] encoded = { "TQ==", "TQ", "TWE=", "TWE", "TWFu", "c3VyZS4=", "c3VyZS4", "ZWFzdXJlLg==", "ZWFzdXJlLg==", "YXN1cmUu", "YW55IGNhcm5hbCBwbGVhc3VyZS4=", "YW55IGNhcm5hbCBwbGVhc3VyZS4" };
		String[] expected= { "M"   , "M" , "Ma"  , "Ma" , "Man" , "sure."   , "sure."  , "easure."     , "easure."     , "asure."  , "any carnal pleasure."        , "any carnal pleasure." };
		for (int i = 0; i < encoded.length; i++) {
			String actual = new String(BaseN.base64Decode(encoded[i]), StandardCharsets.UTF_8);
			assertEquals(expected[i], actual);
		}
	}
}
