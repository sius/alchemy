package liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb;

import jakarta.xml.bind.*;
import liquer.alchemy.xmlcrypto.crypto.xml.URLKeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AssertionReaderTest;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.core.AssertionFactory;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.jaxb.model.AssertionType;
import liquer.alchemy.xmlcrypto.support.BaseN;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

public class JAXBModelTest {

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
    void testReadAssertion() {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN)))) {

            JAXBContext jaxbContext = JAXBContext.newInstance(AssertionType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            JAXBElement<AssertionType> elem =
                    jaxbUnmarshaller.unmarshal(
                            XMLInputFactory.newFactory().createXMLStreamReader(in),
                            AssertionType.class);

            AssertionType assertion = elem.getValue();
            XMLGregorianCalendar issuerInstant = assertion.getIssueInstant();
            System.out.println(issuerInstant.toString());

        } catch (IOException | JAXBException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testWriteAssertion() {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(SAML_TOKEN)))) {

            JAXBContext jaxbContext = JAXBContext.newInstance(AssertionType.class );
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            JAXBElement<AssertionType> elem =
                    jaxbUnmarshaller.unmarshal(
                            XMLInputFactory.newFactory().createXMLStreamReader(in),
                            AssertionType.class );

            AssertionType assertion = elem.getValue();
            XMLGregorianCalendar issuerInstant = assertion.getIssueInstant();
            System.out.println(issuerInstant.toString());

            JAXBContext context = JAXBContext.newInstance(AssertionType.class);

            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Write to System.out
            m.marshal(elem, System.out);
        } catch (IOException | JAXBException | XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
