# Alchemy

Java Playground

Modules:
- `alembic` (an alchemical stills used for distilling)
- `alkahest` (an universal solvent)
- `athanor` (alchemical furnace)
- `crypto`
- `xmlcrypto`

- Dynamic Typing
    - Deep cloning and convert almost any type into an other

- JSON 
    - parse and write JSON
    
- Cryptography
    - Crypto Limericks
    - SigV4
    - Sign and verify JWT Tokens
    - Sign and verify XML Documents
    - SAML2 Token Validation

## Cryptography

###  Sign and verify XML Documents

#### Create Signed Xml

Example:
```java
public class XmlSignerApp {
  
  int main() throws IOException {

        KeyInfo info = new URLKeyInfo(getClass().getResource("/client.pem"));
        String xml =  IOUtil.toString(getClass().getResource("/library.xml"));
        String expected =  IOUtil.toString(getClass().getResource("/library_signed.xml"));
        
        XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.setSigningKey(info.getKey());
        xmlSigner.addReference("//*[local-name(.)='book']");
        xmlSigner.computeSignature(xml);
        
        final String actual = xmlSigner.getSignedXml();

        Assert.assertEquals(expected, actual);
  }
}
```
#### Verify Signed Xml

Example:
```java
public class XmlVerifierApp {

    int main() throws IOException {

        KeyInfo publicKeyInfo = new URLKeyInfo(getClass().getResource("/client_public.pem"));
        String signedXml = IOUtil.toString(getClass().getResource("/library_signed.xml"));
        Document doc = XmlUtil.toDocument(signedXml);
        Node signature = XPathSelector.selectFirst(doc, "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");
        
        XmlSigner xmlSigner = new XmlSigner();
        xmlSigner.loadSignature(signature);
        xmlSigner.setKeyInfoProvider(publicKeyInfo);
        boolean valid = xmlSigner.verifySignature(signedXml);
        
        if (!valid) {
            String validationErrors = xmlSigner.getValidationErrors().stream().collect(
                    Collectors.joining("\n"));
            System.out.println(validationErrors);
        }
        Assert.assertTrue(valid);
    }
}
``` 

### SAML2 Token Validation 

Example:
```java
public class SAML2ValidatorApp {
    
    int main() {
        
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN))) ) {
            String signedXml = IOUtil.toString(in);
            
            final Assertion assertion = AssertionFactory.of(signedXml, new DefaultNamespaceContextMap());
            SamlValidationResult result = assertion.verifySignature(signedXml);

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
```
