package liquer.alchemy.crypto.xml;

import liquer.alchemy.alkahest.Func2;
import liquer.alchemy.alkahest.Func4;
import liquer.alchemy.crypto.xml.saml.Assertion;
import liquer.alchemy.crypto.xml.saml.DefaultNamespaceContextMap;
import liquer.alchemy.crypto.xml.saml.EnvelopedKeyInfo;
import liquer.alchemy.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.alembic.BaseN;
import liquer.alchemy.alembic.IOSupport;
import liquer.alchemy.crypto.xml.saml.jaxb.model.AssertionType;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class XmlSignerTest {


    @Test
    public void signXml() throws IOException {

        final KeyInfo info = new URLKeyInfo(getClass().getResource("/client.pem"));
        final String xml =  IOSupport.toString(getClass().getResource("/library.xml"));
        final String expected =  IOSupport.toString(getClass().getResource("/library_signed.xml"));

        long start = System.currentTimeMillis();
        XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.setSigningKey(info.getKey());
        xmlSigner.addReference("//*[local-name(.)='book']");
        xmlSigner.computeSignature(xml);
        long end = System.currentTimeMillis();
        System.out.println("sign xml with closure: " + (end - start) + " ms");
        final String actual = xmlSigner.getSignedXml();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void signXmlWithClosure() throws IOException {

        final Func4<XmlSigner, KeyInfo, String, String, String> sign =
            (xmlSigner, info, ref, xml) -> {
                xmlSigner.setSigningKey(info.getKey());
                xmlSigner.addReference(ref);
                xmlSigner.computeSignature(xml);
                return xmlSigner.getSignedXml();
            };

        final long start = System.currentTimeMillis();
        final String actual = sign
            .apply(new XmlSigner())
            .apply(new URLKeyInfo(getClass().getResource("/client.pem")))
            .apply("//*[local-name(.)='book']")
            .apply(IOSupport.toString(getClass().getResource("/library.xml")));

        System.out.println("sign xml: " + (System.currentTimeMillis() - start) + " ms");
        final String expected =  IOSupport.toString(getClass().getResource("/library_signed.xml"));
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void validateXml() throws IOException {
        final KeyInfo publicKeyInfo = new URLKeyInfo(getClass().getResource("/client_public.pem"));

        final String signedXml = IOSupport.toString(getClass().getResource("/library_signed.xml"));
        final Document doc = XmlSupport.toDocument(signedXml);
        final Node signature = XPathSupport.selectFirst(doc, "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");

        final long start = System.currentTimeMillis();
        final XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.loadSignature(signature);
        xmlSigner.setKeyInfoProvider(publicKeyInfo);
        final ValidationResult result = xmlSigner.validateSignature(signedXml);

        System.out.println("validate xml: " + (System.currentTimeMillis() - start) + " ms");

        if (!result.isValidToken()) {
            final String validationErrors =
                    String.join("\n", result.getValidationErrors());
            System.out.println(validationErrors);
        }
        Assert.assertTrue(result.isValidSignature());
    }

    @Test
    public void validateXmlWithClosure() throws IOException {

        final Func4<XmlSigner, KeyInfo, String, String, ValidationResult> validate =
            (xmlSigner, info, signedXml, xpath) -> {
                final Document doc = XmlSupport.toDocument(signedXml);
                final Node signature = XPathSupport.selectFirst(doc, xpath);
                xmlSigner.setKeyInfoProvider(info);
                xmlSigner.loadSignature(signature);
                return xmlSigner.validateSignature(signedXml);
            };

        final long start = System.currentTimeMillis();
        final ValidationResult result = validate
            .apply(new XmlSigner())
            .apply(new URLKeyInfo(getClass().getResource("/client_public.pem")))
            .apply(IOSupport.toString(getClass().getResource("/library_signed.xml")))
            .apply("/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");

        System.out.println("validate xml with closure: " + (System.currentTimeMillis() - start) + " ms");

        if (!result.isValidToken()) {
            final String validationErrors =
                    String.join("\n", result.getValidationErrors());
            System.out.println(validationErrors);
        }
        Assert.assertTrue(result.isValidSignature());
    }

    @Test
    public void validateAssertion() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN))) ) {

            final long start = System.currentTimeMillis();
            final String signedXml = IOSupport.toString(in);
            final Assertion assertion = AssertionFactory.newReader(signedXml, new DefaultNamespaceContextMap());
            final ValidationResult result = assertion.validateSignature(signedXml);

            System.out.println("validate assertion: " + (System.currentTimeMillis() - start) + " ms");

            if (!result.isValidToken()) {
                final String validationErrors =
                    String.join("\n", result.getValidationErrors());
                System.out.println(validationErrors);
            }

            Assert.assertTrue(result.isValidSignature());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateAssertionWithClosure() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN))) ) {

            final Func2<String, NamespaceContext, ValidationResult> validate =
                (signedXml, nsCtx) ->
                        AssertionFactory.newReader(signedXml, nsCtx)
                                .validateSignature(signedXml);

            final long start = System.currentTimeMillis();
            final ValidationResult result = validate
                .apply(IOSupport.toString(in))
                .apply(new DefaultNamespaceContextMap());

            System.out.println("validate assertion with closure: " + (System.currentTimeMillis() - start) + " ms");

            if (!result.isValidToken()) {
                final String validationErrors =
                    String.join("\n", result.getValidationErrors());
                System.out.println(validationErrors);
            }

            Assert.assertTrue(result.isValidSignature());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateAssertionWithJaxB() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN)))) {

            final long start = System.currentTimeMillis();
            final String signedXml = IOSupport.toString(in);
            final JAXBContext jaxbContext = JAXBContext.newInstance(AssertionType.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Document doc = XmlSupport.toDocument(signedXml);

            final JAXBElement<AssertionType> elem = (JAXBElement) jaxbUnmarshaller.unmarshal(doc);
            final AssertionType assertion = elem.getValue();

            final NamespaceContext namespaceContext =
                    new DefaultNamespaceContextMap();

            final BiFunction<Node, String, Stream<Node>> select =
                    XPathSupport.getSelectStreamClosure(namespaceContext);
            final Node signatureNode = select.apply(doc.getDocumentElement(), "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']")
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No Signature fount"));

            final XmlSignerOptions options = new XmlSignerOptions();
            options.setNamespaceContext(new DefaultNamespaceContextMap());

            final XmlSigner xmlSigner = new XmlSigner(options);
            xmlSigner.setKeyInfoProvider(new EnvelopedKeyInfo());
            xmlSigner.loadSignature(signatureNode);

            final ValidationResult result = xmlSigner.validateSignature(signedXml);

            System.out.println("validate assertion: " + (System.currentTimeMillis() - start) + " ms");

            if (!result.isValidToken()) {
                final String validationErrors =
                        String.join("\n", result.getValidationErrors());
                System.out.println(validationErrors);
            }

            Assert.assertTrue(result.isValidSignature());

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }
}
