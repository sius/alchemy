package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.XmlSigner;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Assertion;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AssertionBuilder;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Signature;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Assertion Factory
 */
public final class AssertionFactory {
    private static final Logger LOG = LogManager.getLogger(XmlSigner.class);

    private static final ThreadLocal<SAXParser> SAX_PARSER_THREAD_LOCAL;

    static {
        SAX_PARSER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setNamespaceAware(true);
                return saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                throw new IllegalStateException("failed to create " + SAXParser.class.getSimpleName(), e);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> SAX_PARSER_THREAD_LOCAL.remove()));
    }

    private static SAXParser saxParser() {
        return SAX_PARSER_THREAD_LOCAL.get();
    }

    public static Assertion newReader(InputStream in) {
        return newReader(in, null);
    }

    public static Assertion newReader(InputStream in, XmlSignerOptions options) {
        try {
            return newReader(IOSupport.toString(in), options);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    public static Assertion newReader(String xml, XmlSignerOptions options) {
        if (xml == null) {
            throw new NullPointerException("Argument xml cannot be null");
        }
        final SAXAssertionReader assertionReader = new SAXAssertionReader(xml, options);
        try {
            saxParser().parse(new ByteArrayInputStream(xml.getBytes()), assertionReader);
        } catch (IOException | SAXException | IllegalStateException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }

        return assertionReader;
    }

    public static AssertionBuilder newBuilder() {
        return new JAXBAssertionBuilder();
    }

    public static Signature newSignatureBuilder(String xml, NamespaceContext context) {
        if (xml == null) {
            throw new NullPointerException("Argument xml cannot be null");
        }

        final SAXAssertionReader.SAXSignatureReader signatureReader =
            new SAXAssertionReader.SAXSignatureReader(xml, new XmlSignerOptions().namespaceContext(context));

        try {
            saxParser().parse(new ByteArrayInputStream(xml.getBytes()), signatureReader);
        } catch (IOException | SAXException | IllegalStateException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }

        return signatureReader;
    }

    public static SAXIdAttributeReader newIdAttributeReader(String xml, String id, String idAttribute) throws SAXException {
        SAXIdAttributeReader idAttributeReader = new SAXIdAttributeReader(id, idAttribute);
        try {
            saxParser().parse(new ByteArrayInputStream(xml.getBytes()), idAttributeReader);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return idAttributeReader;
    }
}
