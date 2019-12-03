package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlSignerOptions;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Assertion;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.AssertionBuilder;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Signature;
import liquer.alchemy.xmlcrypto.functional.Try;
import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Assertion Factory
 */
public final class AssertionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AssertionFactory.class);

    // Reused as EntityResolver and/or ErrorHandler
    private static final SAXAssertionReader ASSERTION_HANDLER;
    private static final ThreadLocal<SAXParser> ASSERTION_PARSER_THREAD_LOCAL;
    private static final ThreadLocal<DocumentBuilder> ASSERTION_DOCUMENT_BUILDER_THREAD_LOCAL;
    private static final ThreadLocal<SAXParser> SAX_PARSER_THREAD_LOCAL;

    static {
        //System.setProperty(
          //  "javax.xml.transform.TransformerFactory",
            //"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        SAX_PARSER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setNamespaceAware(true);
                return saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                throw new IllegalStateException("failed to create " + SAXParser.class.getSimpleName(), e);
            }
        });

        ASSERTION_HANDLER = new SAXAssertionReader();
        ASSERTION_PARSER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                final InputSource schemaSource = Try.of(() -> ASSERTION_HANDLER.resolveEntity(null, Identifier.SAML2_NS_URI)).orElseGet(null);
                final SchemaFactory schemaFactory = SchemaFactory.newInstance(Identifier.XML_SCHEMA_LANGUAGE);
                schemaFactory.setErrorHandler(ASSERTION_HANDLER);
                schemaFactory.setResourceResolver(ASSERTION_HANDLER);
                final Schema validationSchema = schemaFactory.newSchema(new SAXSource(schemaSource));
                final SAXParserFactory assertionParserFactory = SAXParserFactory.newInstance();

                assertionParserFactory.setNamespaceAware(true);

                /* https://docs.oracle.com/javase/7/docs/api/javax/xml/parsers/SAXParserFactory.html
                Specifies that the parser produced by this code will validate documents as they are parsed. By default the value of this is set to false.
                Note that "the validation" here means a validating parser as defined in the XML recommendation. In other words, it essentially just controls the DTD validation. (except the legacy two properties defined in JAXP 1.2.)

                To use modern schema languages such as W3C XML Schema or RELAX NG instead of DTD, you can configure your parser to be a non-validating parser by leaving the setValidating(boolean) method false, then use the setSchema(Schema) method to associate a schema to a parser.

                Parameters:
                validating - true if the parser produced by this code will validate documents as they are parsed; false otherwise.
                 */
                assertionParserFactory.setValidating(false);
                assertionParserFactory.setSchema(validationSchema);
                assertionParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                return assertionParserFactory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                throw new IllegalStateException("failed to create " + SAXParser.class.getSimpleName(), e);
            }
        });

        ASSERTION_DOCUMENT_BUILDER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                final DocumentBuilderFactory assertionDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
                assertionDocumentBuilderFactory.setNamespaceAware(true);
                assertionDocumentBuilderFactory.setValidating(false);
                assertionDocumentBuilderFactory.setAttribute(Identifier.JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
                assertionDocumentBuilderFactory.setAttribute(Identifier.JAXP_SCHEMA_SOURCE,
                    Try.of(() -> ASSERTION_HANDLER.resolveEntity("", Identifier.SAML2_NS_URI)).orElseGet(null));
                assertionDocumentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "file,jar");
                assertionDocumentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file,jar");
                assertionDocumentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                assertionDocumentBuilderFactory.setExpandEntityReferences(true);
                return assertionDocumentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException("Failed to create validating DocumentBuilder " + DocumentBuilder.class.getSimpleName(), e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            SAX_PARSER_THREAD_LOCAL.remove();
            ASSERTION_PARSER_THREAD_LOCAL.remove();
            ASSERTION_DOCUMENT_BUILDER_THREAD_LOCAL.remove();
        }));
    }

    public static EntityResolver getEntityResolver() { return ASSERTION_HANDLER; }
    public static ErrorHandler getErrorHandler() { return ASSERTION_HANDLER; }

    /**
     *
     * @return the Assertion Schema validating DocumentBuilder
     */
    public static DocumentBuilder assertionDocumentBuilder() {
        final DocumentBuilder ret = ASSERTION_DOCUMENT_BUILDER_THREAD_LOCAL.get();
        ret.setEntityResolver(ASSERTION_HANDLER);
        ret.setErrorHandler(ASSERTION_HANDLER);
        return ret;
    }

    public static Document newAssertionDocument() { return assertionDocumentBuilder().newDocument(); }
    public static Document toAssertionDocument(String xml) throws IOException, SAXException {
        try {
            final Document ret = assertionDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            ret.normalizeDocument();
            return ret;
        } catch (IOException | SAXException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    public static SAXParser assertionParser() {
        return ASSERTION_PARSER_THREAD_LOCAL.get();
    }

    private static SAXParser saxParser() { return SAX_PARSER_THREAD_LOCAL.get(); }

    public static Assertion newReader(InputStream in) throws AssertionException { return newReader(in, null); }

    public static Assertion newReader(InputStream in, XmlSignerOptions options) throws AssertionException {
        try {
            return newReader(IOSupport.toString(in), options);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new AssertionException(e.getMessage(), e);
        }
    }

    public static Assertion newReader(String xml, XmlSignerOptions options) throws AssertionException {
        if (xml == null) {
            throw new NullPointerException("Argument xml cannot be null");
        }
        final SAXAssertionReader assertionReader = new SAXAssertionReader(xml, options);

        try {
            assertionParser().parse(new ByteArrayInputStream(xml.getBytes()), assertionReader, Identifier.SAML2_NS_URI);
        } catch (IOException | SAXException e) {
            LOG.error(e.getMessage(), e);
            throw new AssertionException(e.getMessage(), e);
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
