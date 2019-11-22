package liquer.alchemy.xmlcrypto.app;

import liquer.alchemy.xmlcrypto.crypto.xml.*;
import liquer.alchemy.xmlcrypto.functional.Try;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import liquer.alchemy.xmlcrypto.support.Log;
import liquer.alchemy.xmlcrypto.support.TypedMap;
import liquer.alchemy.xmlcrypto.support.YashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Exemplary Usage:

     # mvn clean package
     # mkdir /d/dev/test
     # cp src/test/resources/*.pem /d/dev/test
     # cp src/test/resources/*.cer /d/dev/test
     # cp src/test/resources/*.xml /d/dev/test
     # TEST_PATH=/d:/dev/test
     # TEST_APP_CLASSPATH="target/xmlcrypto-1.0.0-SNAPSHOT.jar;ext/slf4j-api-1.7.24.jar;ext/logback-classic-1.2.3.jar;ext/logback-core-1.2.3.jar"
     java --class-path $TEST_APP_CLASSPATH \
         liquer.alchemy.xmlcrypto.app.XmlVerifierApp \
         --public-key-cert "$TEST_PATH/publickey.cer" \
         --signed-xml-file "$TEST_PATH/library_signed.xml"
 *
 */
public class XmlVerifierApp {

    private static final Log LOG = new Log(){};
    public static void main(String[] args) {

        final TypedMap tm = new TypedMap(YashMap.of(args));

        try {
            final Path publicKeyFile = Try.of(() ->
                Paths.get(tm.tryStringValue("-c", "--public-key-cert")
                    .orElseThrow(() -> new Exception("Argument --public-key-cert|-c cannot not be empty"))))
                        .orElseThrow();

            final Path xmlFile = Try.of(() ->
                Paths.get(tm.tryStringValue("-f", "--signed-xml-file")
                    .orElseThrow(() -> new Exception("Argument --signed-xml-file|-f cannot not be empty"))))
                        .orElseThrow();

            String signedXml;
            try (InputStream in = new FileInputStream(xmlFile.toFile())) {
                signedXml =  IOSupport.toString(in);
            }

            KeyInfo publicKeyInfo = new FileKeyInfo(publicKeyFile.toFile());
            Document doc = XmlSupport.toDocument(signedXml);
            Node signature = XPathSupport.selectFirst(doc,
                "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']");

            XmlSigner xmlSigner = new XmlSigner();
            xmlSigner.setKeyInfoProvider(publicKeyInfo);
            xmlSigner.loadSignature(signature);
            ValidationResult result = xmlSigner.validateSignature(signedXml);

            LOG.log("validToken: " + result.isValidToken());
            LOG.log("validSignature: " + result.isValidSignature());
            LOG.log("validationErrors:\n" + String.join("\n", result.getErrors()));
         } catch (Exception e) {
            LOG.log(e.getMessage());
        }
    }
}