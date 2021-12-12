package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionException;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.Clock;
import liquer.alchemy.xmlcrypto.support.Timer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class AssertionBuilderTest {

    @Test
    void testCreateBase64GZippedAssertion_RSAWithSHA1() {
        final int n = 10;

        Clock.set( (t) -> {
            Function<Timer, String> f = (ti) -> {
                final String ret = AssertionFactory.newBuilder()
                    .issuer("liquer")
                    .conditionsMinutes(10, "http://liquer.io")
                    .addStatement("http://liquer.io/7k/user",
                        Arrays.asList(
                            "john.snow@winterfell.7k"
                        )).buildBase64GZippedToken(
                        new XmlSignerOptions(),
                        new URLKeyInfo(getClass().getResource("/publickey.cer")),
                        new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")));
                ti.stop("build token");
                return ret;
            };

            List<String> tokens = IntStream.range(0, n).mapToObj(i -> "\"" + f.apply(t) + "\"")
                .collect(Collectors.toList());

        }).go().stop("done").println("createBase64GZippedAssertion_RSAWithSHA1");
    }

    @Test
    void testCreateBase64GZippedAssertion_RSAWithSHA512() {
        final int n = 10;

        Clock.set( (t) -> {
            Function<Timer, String> f = (ti) -> {
                final String ret = AssertionFactory.newBuilder()
                        .issuer("liquer")
                        .conditionsMinutes(10, "http://liquer.io")
                        .addStatement("http://liquer.io/7k/user",
                                Arrays.asList(
                                        "john.snow@winterfell.7k"
                                )).buildBase64GZippedToken(
                                new XmlSignerOptions()
                                    .digestAlgorithm(Identifier.SHA512)
                                    .signatureAlgorithm(Identifier.RSA_WITH_SHA512),
                                new URLKeyInfo(getClass().getResource("/publickey.cer")),
                                new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")));
                ti.stop("build token");
                return ret;
            };

            List<String> tokens = IntStream.range(0, n).mapToObj(i -> "\"" + f.apply(t) + "\"")
                    .collect(Collectors.toList());

        }).go().stop("done").println("createBase64GZippedAssertion_RSAWithSHA512");
    }

    @Test
    void testCreateAndValidate_RSAWithSHA1() {
        Clock.set( (t) -> {
            String signedXml = AssertionFactory.newBuilder()
                .id("a123") // try an invalid NCName, e.g. "123"
                .issuer("liquer")
                .conditionsMinutes(10, "http://liquer.io")
                .addStatement("http://liquer.io/7k/user",
                    Arrays.asList(
                        "john.snow@winterfell.7k"
                    )).buildSigned(
                    new XmlSignerOptions(),
                    new URLKeyInfo(getClass().getResource("/publickey.cer")),
                    new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")));

            t.stop("build assertion");
            System.out.println(signedXml);

            final Assertion assertion;
            try {
                assertion = AssertionFactory.newReader(signedXml,
                    new XmlSignerOptions().namespaceContext(SamlNamespaceContext.newInstance()));
                final ValidationResult result = assertion.validateSignature();

                t.stop("read and validate assertion");

                if (!result.isValidToken()) {
                    final String validationErrors =
                            String.join("\n", result.getErrors());
                    fail(validationErrors);
                }

                assertTrue(result.isValidToken());
            } catch (AssertionException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

        }).go().stop("done").println("createAndValidate_RSAWithSHA1");
    }

    @Test
    public void testCreateAndValidate_RSAWithSHA256() {
        Clock.set( (t) -> {
            String signedXml = AssertionFactory.newBuilder()
                .issuer("liquer")
                .conditionsMinutes(10, "http://liquer.io")
                .addStatement("http://liquer.io/7k/user",
                    Arrays.asList(
                        "john.snow@winterfell.7k"
                    )).buildSigned(
                    new XmlSignerOptions()
                        .canonicalizationAlgorithm(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS)
                        .digestAlgorithm(Identifier.SHA256)
                        .signatureAlgorithm(Identifier.RSA_WITH_SHA256),
                    new URLKeyInfo(getClass().getResource("/publickey.cer")),
                    new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")));

            t.stop("build assertion");

            final Assertion assertion;
            try {
                assertion = AssertionFactory.newReader(signedXml,
                        new XmlSignerOptions().namespaceContext(SamlNamespaceContext.newInstance()));

                final ValidationResult result = assertion.validateSignature();

                t.stop("read and validate assertion");

                if (!result.isValidToken()) {
                    final String validationErrors =
                            String.join("\n", result.getErrors());
                    fail(validationErrors);
                }

                assertTrue(result.isValidToken());
            } catch (AssertionException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }).go().stop("done").println("createAndValidate_RSAWithSHA256");
    }

    @Test
    public void testCreateAndValidate_RSAWithSHA512() {
        Clock.set( (t) -> {
            String signedXml = AssertionFactory.newBuilder()
                    .issuer("liquer")
                    .conditionsMinutes(10, "http://liquer.io")
                    .addStatement("http://liquer.io/7k/user",
                            Arrays.asList(
                                    "john.snow@winterfell.7k"
                            )).buildSigned(
                            new XmlSignerOptions()
                                    .canonicalizationAlgorithm(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS)
                                    .digestAlgorithm(Identifier.SHA512)
                                    .signatureAlgorithm(Identifier.RSA_WITH_SHA512),
                            new URLKeyInfo(getClass().getResource("/publickey.cer")),
                            new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")));

            t.stop("build assertion");

            final Assertion assertion;
            try {
                assertion = AssertionFactory.newReader(signedXml,
                        new XmlSignerOptions().namespaceContext(SamlNamespaceContext.newInstance()));

                final ValidationResult result = assertion.validateSignature();

                t.stop("read and validate assertion");

                if (!result.isValidToken()) {
                    final String validationErrors =
                            String.join("\n", result.getErrors());
                    fail(validationErrors);
                }

                assertTrue(result.isValidToken());
            } catch (AssertionException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

        }).go().stop("done").println("createAndValidate_RSAWithSHA512");
    }
}
