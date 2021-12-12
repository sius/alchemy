package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Assertion;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AssertionReaderTest;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.SamlNamespaceContext;
import liquer.alchemy.xmlcrypto.functional.TryableException;
import liquer.alchemy.xmlcrypto.support.BaseN;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

class AssertionFactoryTest {

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
    void testResolveXmlSchemaDtdLocation() {
        final EntityResolver resolver = AssertionFactory.getEntityResolver();

        try {
            final InputSource inputSource = resolver.resolveEntity("", Identifier.XML_SCHEMA_DTD_LOCATION);
            String actual = IOSupport.toString(inputSource.getByteStream());
            assertNotNull(actual);
        } catch (SAXException | IOException | TryableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testResolveXmldsigSchemaLocation() {
        final EntityResolver resolver = AssertionFactory.getEntityResolver();

        try {
            final InputSource inputSource = resolver.resolveEntity("", Identifier.XMLDSIG_SCHEMA_LOCATION);
            String actual = IOSupport.toString(inputSource.getByteStream());
            assertNotNull(actual);
        } catch (SAXException | IOException | TryableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testResolveXmlencSchemaLocation() {
        final EntityResolver resolver = AssertionFactory.getEntityResolver();

        try {
            final InputSource inputSource = resolver.resolveEntity("", Identifier.XMLENC_SCHEMA_LOCATION);
            String actual = IOSupport.toString(inputSource.getByteStream());
            assertNotNull(actual);
        } catch (SAXException | IOException | TryableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testNewReader() {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN)))) {

            String xml = IOSupport.toString(in);
            final Assertion assertion = AssertionFactory.newReader(xml,
                new XmlSignerOptions()
                    .namespaceContext(SamlNamespaceContext.newInstance()));

            assertEquals(xml, assertion.toString());

        } catch (IOException | AssertionException e) {
            fail(e.getMessage());
        }
    }
}