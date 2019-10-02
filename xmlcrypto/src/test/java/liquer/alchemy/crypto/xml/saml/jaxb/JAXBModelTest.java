package liquer.alchemy.crypto.xml.saml.jaxb;


import liquer.alchemy.alembic.BaseN;
import liquer.alchemy.crypto.xml.TestConstants;
import liquer.alchemy.crypto.xml.saml.jaxb.model.AssertionType;
import org.junit.Test;

import javax.xml.bind.*;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class JAXBModelTest {

    @Test
    public void readAssertion() {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN)))) {

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
    public void writeAssertion() {
        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(BaseN.base64Decode(TestConstants.SAML_TOKEN)))) {

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
