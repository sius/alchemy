package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.functional.Func4;
import liquer.alchemy.xmlcrypto.functional.Try;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;

public class XmlSignerTest {

    @Test
    public void testSignXml() throws IOException {
        final KeyInfo info = new URLKeyInfo(getClass().getResource("/private-pkcs8.pem"));
        final String xml =  IOSupport.toString(getClass().getResource("/library.xml"));
        final String expected =  IOSupport.toString(getClass().getResource("/library-signed.xml"));

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
    public void testSignXmlWithClosure() throws IOException {

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
            .apply(new URLKeyInfo(getClass().getResource("/private-pkcs8.pem")))
            .apply("//*[local-name(.)='book']")
            .apply(IOSupport.toString(getClass().getResource("/library.xml")));

        System.out.println("sign xml: " + (System.currentTimeMillis() - start) + " ms");
        final String expected =  IOSupport.toString(getClass().getResource("/library-signed.xml"));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidateXml() throws IOException {
        final KeyInfo publicKeyInfo = new URLKeyInfo(getClass().getResource("/publickey.cer"));
        final String signedXml = IOSupport.toString(getClass().getResource("/library-signed.xml"));
        final Document doc = Try.of(() -> XmlSupport.toDocument(signedXml)).orElseThrow();

        final Node signature = XPathSupport.selectFirst(doc,
            "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");
        final long start = System.currentTimeMillis();
        final XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.loadSignature(signature);
        xmlSigner.setKeyInfoProvider(publicKeyInfo);
        final ValidationResult result = xmlSigner.validateSignature(signedXml);

        System.out.println("validate xml: " + (System.currentTimeMillis() - start) + " ms");

        if (!result.isValidToken()) {
            final String validationErrors =
                    String.join("\n", result.getErrors());
            Assert.fail(validationErrors);
        }
        Assert.assertTrue(result.isValidSignature());
    }

    @Test
    public void testValidateXmlWithClosure() throws IOException {

        final Func4<XmlSigner, KeyInfo, String, String, ValidationResult> validate =
            (xmlSigner, info, signedXml, xpath) -> {
                final Document doc =
                    Try.of(() -> XmlSupport.toDocument(signedXml))
                        .orElseThrow(() -> new IllegalArgumentException("Illegal argument signedXml"));
;
                final Node signature = XPathSupport.selectFirst(doc, xpath);
                xmlSigner.setKeyInfoProvider(info);
                xmlSigner.loadSignature(signature);
                return xmlSigner.validateSignature(signedXml);
            };

        final long start = System.currentTimeMillis();
        final ValidationResult result = validate
            .apply(new XmlSigner())
            .apply(new URLKeyInfo(getClass().getResource("/publickey.cer")))
            .apply(IOSupport.toString(getClass().getResource("/library-signed.xml")))
            .apply("/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");

        System.out.println("validate xml with closure: " + (System.currentTimeMillis() - start) + " ms");

        if (!result.isValidToken()) {
            final String validationErrors =
                    String.join("\n", result.getErrors());
            Assert.fail(validationErrors);
        }
        Assert.assertTrue(result.isValidSignature());
    }
}
