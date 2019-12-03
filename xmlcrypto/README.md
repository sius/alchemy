# xmlcrypto

**NOT READY FOR PRODUCTION**

- Generate RSA Keys with OpenSSL
- Sign and validate generic XML Documents
- Create, sign and validate SAML2 Assertions

## Generate RSA Keys with OpenSSL

[Keys used for the examples](./keys):
```
mkdir keys
# Generate PKCS#1 Key
openssl genrsa -des3 -out keys/private.pem 4096
# passphrase: test

# 1. Convert PKCS#1 to PKCS#8
# Used to sign xml files
# 1.1 unencrypted key
openssl pkcs8 -in keys/private.pem -topk8 -nocrypt -out keys/private-pkcs8.pem
# 1.2 encrypted private key 
openssl pkcs8 -in keys/private.pem -topk8 -out keys/private-encrypted-pkcs8.pem
# passphrase: test
# encryption password: test

# 2. Export the RSA Public Key
openssl rsa -in keys/private.pem -pubout -out keys/public.pem
# passphrase: test

# 3. Create a X.509 public key certificate from the public key
# Used to verify the signed xml files
openssl req -new -x509 -key keys/private.pem -out keys/publickey.cer -days 365
```

##  Sign and validate generic XML Documents

### Create a generic Signed Xml

[Example](./src/test/java/liquer/alchemy/xmlcrypto/app/XmlSignerApp.java)

Exemplary Usage:

```bash
mvn clean package
mkdir ~/dev/test
cp src/test/resources/*.pem ~/dev/test
cp src/test/resources/*.cer ~/dev/test
cp src/test/resources/*.xml ~/dev/test
TEST_PATH=~/dev/test
TEST_APP_CLASSPATH="target/xmlcrypto-1.0.0-SNAPSHOT.jar;ext/log4j-api-2.4.jar;ext/log4j-core-2.4.jar;ext/hamcrest-core-1.3.jar"
java --class-path $TEST_APP_CLASSPATH \
    liquer.alchemy.xmlcrypto.app.XmlSignerApp \
    --private-key-file "$TEST_PATH/private-pkcs8.pem" \
    --xml-file "$TEST_PATH/library.xml" \
    --x-path-expression "//*[local-name(.)='book']" \
    --out-file "$TEST_PATH/library_signed.xml"
```

### Validate a generic Signed Xml

[Example](./src/test/java/liquer/alchemy/xmlcrypto/app/XmlVerifierApp.java)

Exemplary Usage:

```bash
# mvn clean package
# mkdir ~/dev/test
# cp src/test/resources/*.pem ~/dev/test
# cp src/test/resources/*.cer ~/dev/test
# cp src/test/resources/*.xml ~/dev/test
# TEST_PATH=~/dev/test
# TEST_APP_CLASSPATH="target/xmlcrypto-1.0.0-SNAPSHOT.jar;ext/log4j-api-2.4.jar;ext/log4j-core-2.4.jar;ext/hamcrest-core-1.3.jar"
java --class-path $TEST_APP_CLASSPATH \
    liquer.alchemy.xmlcrypto.app.XmlVerifierApp \
    --public-key-cert "$TEST_PATH/publickey.cer" \
    --signed-xml-file "$TEST_PATH/library_signed.xml"
```


## Read, write, create, sign and validate SAML2 Assertions

- [Validate SAML2 Token](./src/test/java/liquer/alchemy/xmlcrypto/crypto/xml/saml/AssertionReaderTest.java)
- [Read and write assertions wit JAXB](./src/test/java/liquer/alchemy/xmlcrypto/crypto/xml/saml/jaxb/JAXBModelTest.java)
- [Create signed assertions with JAXB and `XmlSigner`](./src/test/java/liquer/alchemy/xmlcrypto/crypto/xml/saml/AssertionBuilderTest.java):
- [Validate signed assertions with JAXB and `XmlSigner`](./src/test/java/liquer/alchemy/xmlcrypto/crypto/xml/saml/AssertionReaderTest.java):

# Common issues

- End-Of-Line problems: Linux (`\n`), Windows (`\r\n`), older Mac ?? (`\r`)
- Limited cryptographic strength policy files

# Canonicalization (C14N)
- Remove any XML declaration and document type declarations
- Encode document in UTF-8
- Expand entities to their character equivalent
- Replace CDATA sections with their character equivalent
- Encode the special XML entities &lt; &gt; &quot;
- Normalize attribute values, as if by a validating parser
- Open empty elements with start and end tags
- Sort namespace declarations and attributes

## Solve limited cryptographic strength policy files

1. Install unlimited jurisdiction strength policy files (IBM JDK)
2. Use appropriate JDK with an unlimited strength policy (Open JDK)

## Generate JAXB Assertion model with xjc
 
Choose one!

### field accessors

xjc \
-disableXmlSecurity \
-b saml-2.0-os/binding.xml \
-d src/main/java \
-p liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model \
saml-2.0-os/saml-schema-assertion-2.0.xsd

### property accessors

xjc \
-disableXmlSecurity \
-Xpropertyaccessors \
-b saml-2.0-os/binding.xml \
-d src/main/java \
-p liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model \
saml-2.0-os/saml-schema-assertion-2.0.xsd


## DOCS
- [OWASP Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SAML_Security_Cheat_Sheet.html)
- ["On Breaking SAML: Be Whoever You Want to Be", Juraj Somorovsky, Andreas Mayer, Jorg Schwenk, Marco Kampmann, Meiko jensen, 2012](https://www.usenix.org/system/files/conference/usenixsecurity12/sec12-final91.pdf) - [A copy here](docs/sec12-final91.pdf)
- ["Secure SAML validation to prevent XML signature wrapping attacks", Pawel Krawczyk](https://arxiv.org/ftp/arxiv/papers/1401/1401.7483.pdf) - [A copy here](docs/1401.7483.pdf)
- ["On the Effectiveness of XML Schema Validation for Countering XML Signature Wrapping Attacks", Meiko Jensen, Christopher Meyer, Juraj Somorovsky, and Jörg Schwenk, 2013](https://www.nds.ruhr-uni-bochum.de/media/nds/veroeffentlichungen/2013/03/25/paper.pdf) - [A copy here](docs/paper.pdf)
- ["XSpRES: Robust and Effective XML Signatures for Web Services", Christian Mainka, Meiko Jensen, Luigi Lo Iacono, and Jörg Schwenk, 2012](https://www.nds.ruhr-uni-bochum.de/media/nds/veroeffentlichungen/2012/07/24/CLOSER_XSpRES.pdf) - [A copy here](docs/CLOSER_XSpRES.pdf)
- ["XML Signature Syntax and Processing Version 1.1", W3C, 2008](https://www.w3.org/TR/xmldsig-core/)
- [White Space in XML Documents](http://usingxml.com/Basics/XmlSpace)