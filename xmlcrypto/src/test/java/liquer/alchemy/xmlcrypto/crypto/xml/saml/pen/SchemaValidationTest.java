package liquer.alchemy.xmlcrypto.crypto.xml.saml.pen;

import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Assertion;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.SamlNamespaceContext;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionException;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SchemaValidationTest {

    @Test
    void testReadAssertionWithoutSignature_isValid() throws IOException, AssertionException {
        final String xml = IOSupport.toString(
            this.getClass().getResourceAsStream("/pen/assertion/Assertion_WithoutSignature.xml"));

        final Assertion assertion = AssertionFactory.newReader(xml,
                new XmlSignerOptions()
                        .namespaceContext(SamlNamespaceContext.newInstance()));

        assertNotNull(assertion);

        final String expectedName = "http://liquer.io/7k/user";
        final String actualName = assertion.getAttributeStatements().get(0).getAttributes().get(0).getName();

        assertEquals(expectedName, actualName);

        final String expectedValue = "john.snow@winterfell.7k";
        final String actualValue = assertion.getAttributeStatements().get(0).getAttributes().get(0).getValues().get(0);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testReadAssertionWrapped_shouldThrowAssertionException() {

        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_Wrapped.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertionWrapped_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertionWrapped_shouldThrowAssertionException");
        } catch (AssertionException e) {
           assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertionEmbedded_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_Embedded.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertionEmbedded_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertionEmbedded_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertionWrappedIntoSignature_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_Wrapped_Into_Signature.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertionWrappedIntoSignature_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertionWrappedIntoSignature_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertionWrappedIntoSignature2_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_Wrapped_Into_Signature2.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertionWrappedIntoSignature2_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertionWrappedIntoSignature2_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertion2References_isValid() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_2_References.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
        } catch (IOException | AssertionException e) {
            fail("testReadAssertion2References_isValid");
        }
    }

    @Test
    public void testReadAssertion2KeyInfo_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_2_KeyInfo.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertion2KeyInfo_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertion2KeyInfo_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertion2SignatureValue_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_2_SignatureValue.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertion2SignatureValue_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertion2SignatureValue_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertion2SignedInfo_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_2_SignedInfo.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertion2SignedInfo_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertion2SignedInfo_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertion2Signature_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(
                    this.getClass().getResourceAsStream("/pen/assertion/Assertion_2_Signature.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertion2Signature_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertion2Signature_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testReadAssertionInvalidRoot_shouldThrowAssertionException() {
        try {
            final String xml = IOSupport.toString(this.getClass().getResourceAsStream("/pen/assertion/Assertion_InvalidRoot.xml"));

            AssertionFactory.newReader(xml,
                    new XmlSignerOptions()
                            .namespaceContext(SamlNamespaceContext.newInstance()));
            fail("testReadAssertionInvalidRoot_shouldThrowAssertionException");
        } catch (IOException e) {
            fail("testReadAssertionInvalidRoot_shouldThrowAssertionException");
        } catch (AssertionException e) {
            assertNotNull(e);
        }
    }
}
