package liquer.alchemy.xmlcrypto.app;

import liquer.alchemy.xmlcrypto.crypto.xml.FileKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.KeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSigner;
import liquer.alchemy.xmlcrypto.functional.Try;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import liquer.alchemy.xmlcrypto.support.Log;
import liquer.alchemy.xmlcrypto.support.TypedMap;
import liquer.alchemy.xmlcrypto.support.YashMap;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  Exemplary Usage:

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

 */
public class XmlSignerApp  {

    private static final Log LOG = new Log(){};
    public static void main(String[] args) {


        final TypedMap tm = new TypedMap(YashMap.of(args));

        try {
            final Path privateKeyFile = Try.of(() ->
                Paths.get(tm.tryStringValue("-p", "--private-key-file")
                    .orElseThrow(() -> new Exception("Argument --private-key-file|-p cannot not be empty"))))
                        .orElseThrow();

            final Path xmlFile = Try.of(() ->
                Paths.get(tm.tryStringValue("-f", "--xml-file")
                .orElseThrow(() -> new Exception("Argument --xml-file|-f cannot not be empty"))))
                    .orElseThrow();

            final Path outFile = Try.of(() ->
                Paths.get(tm.tryStringValue("-o", "--out-file")
                .orElseThrow(() -> new Exception("Argument --out-file|-o cannot not be empty"))))
                    .orElseThrow();

            final String xPathExpression = tm.tryStringValue("-p", "--x-path-expression")
                .orElseThrow(() -> new Exception("Argument --x-path-expression|-p cannot not be empty"));

            final String xml = IOSupport.toString(xmlFile.toFile());

            try (OutputStream out = new FileOutputStream(outFile.toFile())) {
                KeyInfo info = new FileKeyInfo(privateKeyFile.toFile());
                XmlSigner xmlSigner = new XmlSigner();
                xmlSigner.setSigningKey(info.getKey());
                xmlSigner.addReference(xPathExpression);
                xmlSigner.computeSignature(xml);
                out.write(xmlSigner.getSignedXml().getBytes(StandardCharsets.UTF_8));
                LOG.println("Wrote signed xml file to '" + outFile + "'");
            }
        } catch (Exception e) {
            LOG.println(e.getMessage());
        }
    }
}