package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.Clock;
import liquer.alchemy.xmlcrypto.support.Timer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AssertionBuilderTest {


    @Test
    public void testCreateBase64GZippedAssertion_RSAWithSHA1() {
        final int n = 10;

        Clock.set( (t) -> {
            Function<Timer, String> f = (ti) -> {
                final String ret = TestAssertion.SIGNED_ASSERTION.get();
                ti.stop("build token");
                return ret;
            };

            List<String> tokens = IntStream.range(0, n).mapToObj(i -> "\"" + f.apply(t) + "\"")
                .collect(Collectors.toList());

        }).go().stop("done").println("createBase64GZippedAssertion_RSAWithSHA1");

    }

    @Test
    public void testCreateAndValidate_RSAWithSHA1() {
        Clock.set( (t) -> {
            final String signedXml = TestAssertion.SIGNED_ASSERTION.get();

            t.stop("build assertion");

            final Assertion assertion =
                AssertionFactory.newReader(signedXml,
                    new XmlSignerOptions().namespaceContext(new DefaultNamespaceContextMap()));

            final ValidationResult result = assertion.validateSignature();

            t.stop("read and validate assertion");

            if (!result.isValidToken()) {
                final String validationErrors =
                    String.join("\n", result.getErrors());
                Assert.fail(validationErrors);
            }

            Assert.assertTrue(result.isValidToken());

        }).go().stop("done").println("createAndValidate_RSAWithSHA1");
    }

    @Test
    public void testCreateAndValidate_RSAWithSHA256() {
        Clock.set( (t) -> {
            final String signedXml = TestAssertion.SIGNED_ASSERTION.get();

            t.stop("build assertion");

            final Assertion assertion =
                AssertionFactory.newReader(signedXml,
                        new XmlSignerOptions().namespaceContext(new DefaultNamespaceContextMap()));

            final ValidationResult result = assertion.validateSignature();

            t.stop("read and validate assertion");

            if (!result.isValidToken()) {
                final String validationErrors =
                    String.join("\n", result.getErrors());
                Assert.fail(validationErrors);
            }

            Assert.assertTrue(result.isValidToken());

        }).go().stop("done").println("createAndValidate_RSAWithSHA256");
    }
}
