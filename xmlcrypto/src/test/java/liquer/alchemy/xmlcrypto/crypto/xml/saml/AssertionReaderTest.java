package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionException;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.BaseN;
import liquer.alchemy.xmlcrypto.support.Clock;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    @BeforeAll
    static void beforeAll() {
        SAML_TOKEN = TOKEN.get();
    }

    @Test
    void testValidateAssertion() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN))) ) {

            String xml = IOSupport.toString(in);
            System.out.println(xml);
            Clock.set((t) -> {
                final Assertion assertion;
                try {
                    assertion = AssertionFactory.newReader(xml,
                        new XmlSignerOptions()
                                .namespaceContext(SamlNamespaceContext.newInstance())
                                .timer(t));
                    final ValidationResult result = assertion.validateSignature();

                    t.stop("read and validate assertion");

                    if (!result.isValidToken()) {
                        final String validationErrors =
                                String.join("\n", result.getErrors());
                        fail(validationErrors);
                    }
                    assertTrue(result.isValidSignature());
                } catch (AssertionException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }).go().stop("done").println("validateAssertion");

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    void testValidateAssertion1() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN))) ) {

            Clock.set( (t) -> {
                final Assertion assertion;
                try {
                    assertion = AssertionFactory.newReader(in,
                            new XmlSignerOptions()
                                    .namespaceContext(SamlNamespaceContext.newInstance())
                                    .timer(t));
                    final ValidationResult result = assertion.validateSignature();

                    t.stop("read and validate assertion");

                    if (!result.isValidToken()) {
                        final String validationErrors =
                                String.join("\n", result.getErrors());
                        fail(validationErrors);
                    }
                } catch (AssertionException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }


            }).go().stop("done").println("validateAssertion1");

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    void testValidateAssertionMultipleTimes() {
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
                        fail(validationErrors);
                    }

                } catch (IOException | AssertionException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

        }).go().stop("done").println("validateAssertionMultipleTimes");
    }
}
