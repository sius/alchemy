package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.BaseN;
import liquer.alchemy.xmlcrypto.support.Clock;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

public class AssertionReaderTest {

    private final static Supplier<String> TOKEN = () ->
        AssertionFactory.newBuilder()
            .issuer("liquer")
            .conditionsMinutes(10, "http://liquer.io")
            .addStatement("http://liquer.io/7k/user",
                    Arrays.asList(
                            "john.snow@winterfell.7k"
                    )).buildBase64GZippedToken(
                    new XmlSignerOptions(),
                    new URLKeyInfo(AssertionReaderTest.class.getResource("/publickey.cer")),
                    new URLKeyInfo(AssertionReaderTest.class.getResource("/private-pkcs8.pem")));

    private static String SAML_TOKEN;

    @BeforeClass
    public static void beforeAll() {
        SAML_TOKEN = TOKEN.get();
    }

    @Test
    public void testValidateAssertion() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN))) ) {

            String xml = IOSupport.toString(in);
            System.out.println(xml);
            Clock.set((t) -> {
                final Assertion assertion = AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance())
                            .timer(t));

                final ValidationResult result = assertion.validateSignature();

                t.stop("read and validate assertion");

                if (!result.isValidToken()) {
                    final String validationErrors =
                        String.join("\n", result.getErrors());
                    Assert.fail(validationErrors);
                }
                Assert.assertTrue(result.isValidSignature());

            }).go().stop("done").println("validateAssertion");

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testValidateAssertion1() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN))) ) {

            Clock.set( (t) -> {
                final Assertion assertion = AssertionFactory.newReader(in,
                        new XmlSignerOptions()
                                .namespaceContext(SamlNamespaceContext.newInstance())
                                .timer(t));

                final ValidationResult result = assertion.validateSignature();

                t.stop("read and validate assertion");

                if (!result.isValidToken()) {
                    final String validationErrors =
                        String.join("\n", result.getErrors());
                    Assert.fail(validationErrors);
                }
            }).go().stop("done").println("validateAssertion1");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateAssertionMultipleTimes() {
        final int len = 10;

        Clock.set( (t) -> {

            for (int i = 1; i <= len; i++) {
                t.stop(i + ". validateAssertion");
                try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN))) ) {

                    t.stop("- read GZIPInputStream");

                    final Assertion assertion = AssertionFactory.newReader(in,
                            new XmlSignerOptions()
                                    .namespaceContext(SamlNamespaceContext.newInstance())
                                    .timer(t));

                    final ValidationResult result = assertion.validateSignature();

                    t.stop("- read and validate assertion");

                    if (!result.isValidToken()) {
                        final String validationErrors =
                                String.join("\n", result.getErrors());
                        Assert.fail(validationErrors);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).go().stop("done").println("validateAssertionMultipleTimes");
    }
}
