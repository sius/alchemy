package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.xml.core.FragmentXMLStreamWriter;
import liquer.alchemy.xmlcrypto.crypto.xml.core.NodeListImpl;
import liquer.alchemy.xmlcrypto.support.StringSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stax.StAXResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class XmlSupport {

    private static final Logger LOG = LogManager.getLogger(XmlSupport.class);

    private static final ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER_THREAD_LOCAL;
    private static final ThreadLocal<Transformer> TRANSFORMER_THREAD_LOCAL;
    private static final ThreadLocal<XMLOutputFactory> XML_OUTPUT_FACTORY_THREAD_LOCAL;

    static {
        System.setProperty(
                "javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        XML_OUTPUT_FACTORY_THREAD_LOCAL = ThreadLocal.withInitial(() -> XMLOutputFactory.newFactory());
        DOCUMENT_BUILDER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
                documentBuilderFactory.setExpandEntityReferences(false);
                documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                return documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage() ,e);
                throw new RuntimeException("Failed to create " + DocumentBuilder.class.getSimpleName(), e);
            }
        });

        TRANSFORMER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                return SAXTransformerFactory.newInstance().newTransformer();
            } catch (TransformerException e) {
                LOG.error(e.getMessage() ,e);
                throw new RuntimeException("Failed to create " + Transformer.class.getSimpleName(), e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            XML_OUTPUT_FACTORY_THREAD_LOCAL.remove();
            DOCUMENT_BUILDER_THREAD_LOCAL.remove();
            TRANSFORMER_THREAD_LOCAL.remove();
        }));
    }

    public static NodeList toNodeList(final Stream<Node> nodeStream) {
        return (nodeStream == null)
            ? new NodeListImpl()
            : new NodeListImpl(nodeStream.collect(Collectors.toList()));
    }

    public static Stream<Node> toStream(final NodeList nodeList) {
        if (nodeList == null) {
            return Stream.empty();
        }
        final Spliterator<Node> spliterator
                = Spliterators
                .spliteratorUnknownSize(new Iterator<Node>() {
                    private final int len = nodeList.getLength();
                    private int index = 0;
                    @Override
                    public boolean hasNext() {
                        return index < len;
                    }

                    @Override
                    public Node next() {
                        try {
                            return nodeList.item(index++);
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            LOG.error(e.getMessage() ,e);
                            throw new NoSuchElementException(e.getMessage());
                        }
                    }
                }, Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);

    }

    public static DocumentBuilder documentBuilder() {
        return DOCUMENT_BUILDER_THREAD_LOCAL.get();
    }

    public static XMLOutputFactory xmlOutputFactory() {
        return XML_OUTPUT_FACTORY_THREAD_LOCAL.get();
    }

    public static Transformer transformer() {
        return TRANSFORMER_THREAD_LOCAL.get();
    }

    public static String stringify(Node node) {

        final StringWriter sw = new StringWriter();
        try {
            final XMLStreamWriter writer = new FragmentXMLStreamWriter(
                xmlOutputFactory().createXMLStreamWriter(sw));
            transformer().transform(new DOMSource(node), new StAXResult(writer));
        } catch (XMLStreamException | TransformerException e) {
            LOG.error(e.getMessage() ,e);
            throw new IllegalStateException(e);
        }
        return sw.toString();
    }

    public static Document newDocument() {
        return documentBuilder().newDocument();
    }

    public static Document toDocument(String xml) throws IOException, SAXException {
        try {
            final Document ret = documentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            ret.normalizeDocument();

            return ret;
        } catch (IOException | SAXException e) {
            LOG.error(e.getMessage() ,e);
            throw e;
        }
    }

    public static Document toWrappedDocument(String wrapperElementName, String prefixes, String xml) throws IOException, SAXException {
        final StringBuilder builder = new StringBuilder();
        builder.append('<');
        builder.append(wrapperElementName);
        if (StringSupport.notNullOrEmpty(prefixes)) {
            builder.append(' ');
            builder.append(prefixes);
        }
        builder.append('>');
        if (StringSupport.notNullOrEmpty(xml)) {
            builder.append(xml);
        }
        builder.append("</");
        builder.append(wrapperElementName);
        builder.append('>');
        return toDocument(builder.toString());
    }

    public static StringBuilder buildSelfClosingEmptyTag(String tagName) {
        return buildSelfClosingEmptyTag(new StringBuilder(), tagName);
    }

    public static StringBuilder buildSelfClosingEmptyTag(StringBuilder builder, String tagName) {
        builder = builder == null ? new StringBuilder() : builder;

        builder.append("<");
        builder.append(tagName);
        return builder.append("/>");
    }

    public static StringBuilder buildEmptyTag(String tagName) {
        return buildEmptyTag(new StringBuilder(), tagName);
    }

    public static StringBuilder buildEmptyTag(StringBuilder builder, String tagName) {
        return buildStartTag(builder, tagName).append(buildEndTag(builder, tagName));
    }

    public static StringBuilder buildEndTag(String tagName) {
        return buildEndTag(new StringBuilder(), tagName);
    }

    public static StringBuilder buildEndTag(StringBuilder builder, String tagName) {
        builder = builder == null ? new StringBuilder() : builder;

        builder.append("</");
        builder.append(tagName);
        return builder.append('>');
    }
    public static StringBuilder buildStartTag(String tagName) {
        return buildStartTag(new StringBuilder(), tagName);
    }

    public static StringBuilder buildStartTag(StringBuilder builder, String tagName) {
        builder = builder == null ? new StringBuilder() : builder;

        builder.append('<');
        builder.append(tagName);
        return builder.append('>');
    }

    public static StringBuilder buildAttribute(String name, String value) {
        return buildAttribute(new StringBuilder(), name, value);
    }

    public static StringBuilder buildAttribute(StringBuilder builder, String name, String value) {
        builder = builder == null ? new StringBuilder() : builder;

        builder.append(name);
        builder.append("=\"");
        builder.append(value);
        return builder.append("\"");
    }
}
