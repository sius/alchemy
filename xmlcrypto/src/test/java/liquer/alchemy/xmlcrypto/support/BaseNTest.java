package liquer.alchemy.xmlcrypto.support;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class BaseNTest {

    @Test
    public void test64Encode() {
        Assert.assertEquals("", BaseN.base64Encode(new byte[0]));
        Assert.assertEquals("", BaseN.base64Encode("".getBytes()));
        Assert.assertEquals("Zg==", BaseN.base64Encode("f".getBytes()));
        Assert.assertEquals("Zm8=", BaseN.base64Encode("fo".getBytes()));
        Assert.assertEquals("Zm9v", BaseN.base64Encode("foo".getBytes()));
        Assert.assertEquals("Zm9vYg==", BaseN.base64Encode("foob".getBytes()));
        Assert.assertEquals("Zm9vYmE=", BaseN.base64Encode("fooba".getBytes()));
        Assert.assertEquals("Zm9vYmFy", BaseN.base64Encode("foobar".getBytes()));
    }

    @Test
    public void base64UfsEncode() {
        Assert.assertEquals("", BaseN.base64Encode("".getBytes()));
        Assert.assertEquals("Zg==", BaseN.base64Encode("f".getBytes()));
        Assert.assertEquals("Zm8=", BaseN.base64Encode("fo".getBytes()));
        Assert.assertEquals("Zm9v", BaseN.base64Encode("foo".getBytes()));
        Assert.assertEquals("Zm9vYg==", BaseN.base64Encode("foob".getBytes()));
        Assert.assertEquals("Zm9vYmE=", BaseN.base64Encode("fooba".getBytes()));
        Assert.assertEquals("Zm9vYmFy", BaseN.base64Encode("foobar".getBytes()));
    }

    @Test
    public void base64UrlEncode() {
        Assert.assertEquals("", BaseN.base64UrlEncode("".getBytes()));
        Assert.assertEquals("Zg", BaseN.base64UrlEncode("f".getBytes()));
        Assert.assertEquals("Zm8", BaseN.base64UrlEncode("fo".getBytes()));
        Assert.assertEquals("Zm9v", BaseN.base64UrlEncode("foo".getBytes()));
        Assert.assertEquals("Zm9vYg", BaseN.base64UrlEncode("foob".getBytes()));
        Assert.assertEquals("Zm9vYmE", BaseN.base64UrlEncode("fooba".getBytes()));
        Assert.assertEquals("Zm9vYmFy", BaseN.base64UrlEncode("foobar".getBytes()));
    }

    @Test
    public void base64Decode() {
        String[] encoded = { "TQ==", "TQ", "TWE=", "TWE", "TWFu", "c3VyZS4=", "c3VyZS4", "ZWFzdXJlLg==", "ZWFzdXJlLg==", "YXN1cmUu", "YW55IGNhcm5hbCBwbGVhc3VyZS4=", "YW55IGNhcm5hbCBwbGVhc3VyZS4" };
        String[] expected= { "M"   , "M" , "Ma"  , "Ma" , "Man" , "sure."   , "sure."  , "easure."     , "easure."     , "asure."  , "any carnal pleasure."        , "any carnal pleasure." };
        for (int i = 0; i < encoded.length; i++) {
            String actual = new String(BaseN.base64Decode(encoded[i]), StandardCharsets.UTF_8);
            Assert.assertEquals(expected[i], actual);
        }
    }
}
