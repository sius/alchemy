package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.BaseN;
import liquer.alchemy.xmlcrypto.support.Clock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class AssertionReaderTest {

    private static String TEST_TOKEN;

    @BeforeClass
    public static void init() {
        TEST_TOKEN = TestAssertion.TOKEN.get();
    }

    @Test
    public void testValidateAssertion() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TEST_TOKEN))) ) {

            Clock.set((t) -> {
                final Assertion assertion = AssertionFactory.newReader(in,
                    new XmlSignerOptions()
                            .namespaceContext(new DefaultNamespaceContextMap())
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

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TEST_TOKEN))) ) {

            Clock.set( (t) -> {
                final Assertion assertion = AssertionFactory.newReader(in,
                        new XmlSignerOptions()
                                .namespaceContext(new DefaultNamespaceContextMap())
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
                try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TEST_TOKEN))) ) {

                    t.stop("- read GZIPInputStream");

                    final Assertion assertion = AssertionFactory.newReader(in,
                            new XmlSignerOptions()
                                    .namespaceContext(new DefaultNamespaceContextMap())
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
