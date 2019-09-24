package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.KeyInfo;
import liquer.alchemy.crypto.URLKeyInfo;
import liquer.alchemy.crypto.saml.Assertion;
import liquer.alchemy.crypto.saml.SamlVerificationResult;
import liquer.alchemy.crypto.saml.core.AssertionFactory;
import liquer.alchemy.util.BaseN;
import liquer.alchemy.util.IOUtil;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class XmlSignerTest {

    @Test
    public void signXml() throws IOException {
        KeyInfo info = new URLKeyInfo(getClass().getResource("/client.pem"));

        String xml =  IOUtil.toString(getClass().getResource("/library.xml"));
        String expected =  IOUtil.toString(getClass().getResource("/library_signed.xml"));

        long start = System.currentTimeMillis();
        XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.setSigningKey(info.getKey());
        xmlSigner.addReference("//*[local-name(.)='book']");
        xmlSigner.computeSignature(xml);
        long end = System.currentTimeMillis();
        System.out.println("sign xml: " + (end - start) + " ms");
        final String actual = xmlSigner.getSignedXml();

        // System.out.println(expected);
        // System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void verifyXml() throws IOException {
        KeyInfo publicKeyInfo = new URLKeyInfo(getClass().getResource("/client_public.pem"));

        String signedXml = IOUtil.toString(getClass().getResource("/library_signed.xml"));
        Document doc = XmlUtil.toDocument(signedXml);
        Node signature = XPathSelector.selectFirst(doc, "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");

        XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.loadSignature(signature);

        long start = System.currentTimeMillis();
        xmlSigner.setKeyInfoProvider(publicKeyInfo);
        boolean valid = xmlSigner.verifySignature(signedXml);
        long end = System.currentTimeMillis();

        System.out.println("verify xml: " + (end - start) + " ms");

        if (!valid) {
            String validationErrors = xmlSigner.getValidationErrors().stream().collect(
                    Collectors.joining("\n"));
            System.out.println(validationErrors);
        }
        Assert.assertTrue(valid);
    }

    @Test
    public void verifyAssertion() {

        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN))) ) {
            String signedXml = IOUtil.toString(in);
            // System.out.println(signedXml);

            final Assertion assertion = AssertionFactory.of(signedXml);

            long start = System.currentTimeMillis();
            SamlVerificationResult result = assertion.verifySignature(signedXml);

            System.out.println("verify assertion signature: " + (System.currentTimeMillis() - start) + " ms");

            if (!result.isValidToken()) {
                String validationErrors = assertion.getXmlSigner().getValidationErrors().stream().collect(
                        Collectors.joining("\n"));
                System.out.println(validationErrors);
            }
            Assert.assertTrue(result.isValidSignature());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
