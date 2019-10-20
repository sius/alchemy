package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;

import java.util.Arrays;
import java.util.function.Supplier;

public class TestAssertion {

    private TestAssertion() { }

    public static final Supplier<String> TOKEN =
        () -> AssertionFactory.newBuilder()
            .issuer("xmlcrypto")
            .conditionsMinutes(10, "http://liquer.io")
            .addStatement("http://xml.liquer.io/identities/id",
                Arrays.asList("peter.pan@neverland.io", "john.snow@winterfell.7k"
            )).buildBase64GZippedToken(
                new XmlSignerOptions(),
                new URLKeyInfo(TestAssertion.class.getResource("/publickey.cer")),
                new URLKeyInfo(TestAssertion.class.getResource("/private-pkcs8.pem")));

    public static final Supplier<String> SIGNED_ASSERTION =
        () -> AssertionFactory.newBuilder()
            .issuer("xmlcrypto")
            .conditionsMinutes(10, "http://liquer.io")
            .addStatement("http://xml.liquer.io/identities/id",
                Arrays.asList("peter.pan@neverland.io", "john.snow@winterfell.7k"
                )).buildSigned(
                new XmlSignerOptions(),
                new URLKeyInfo(TestAssertion.class.getResource("/publickey.cer")),
                new URLKeyInfo(TestAssertion.class.getResource("/private-pkcs8.pem")));
}
