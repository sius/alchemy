package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.xml.core.NodeListImpl;
import liquer.alchemy.crypto.xml.core.EOL;
import liquer.alchemy.crypto.xml.core.FragmentXMLStreamWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static liquer.alchemy.crypto.xml.core.EOL.LF;
import static liquer.alchemy.alembic.StringSupport.notNullOrEmpty;

public final class XmlSupport {

    private static final Logger LOG = LogManager.getLogger(XmlSupport.class);
    private final static DocumentBuilderFactory DBF;

    static {
        DocumentBuilderFactory target = null;
        try {
            target = DocumentBuilderFactory.newInstance();
            target.setNamespaceAware(true);
            target.setExpandEntityReferences(false);
            target.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            LOG.error(e);
            throw new RuntimeException("static init failed", e);
        } finally {
            DBF = target;
        }
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
        final Spliterator<Node> spitr
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
                        return nodeList.item(index++);
                    }
                }, Spliterator.NONNULL);
        return StreamSupport.stream(spitr, false);

    }

    public static String stringify(Node node) {
        return stringify(node, false, LF);
    }

    public static String stringify(Node node, boolean selfClosingTag, EOL eol) {

        final StringWriter sw = new StringWriter();
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            XMLStreamWriter writer = new FragmentXMLStreamWriter(XMLOutputFactory.newFactory()
                    .createXMLStreamWriter(sw));
            t.transform(new DOMSource(node), new StAXResult(writer));
        } catch (TransformerException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static Document toDocument(String xml) {
        try {
            final Document ret = DBF.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            ret.normalizeDocument();

            return ret;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document toWrappedDocument(String wrapperElementName, String prefixes, String xml) {
        final StringBuilder builder = new StringBuilder();
        builder.append('<');
        builder.append(wrapperElementName);
        if (notNullOrEmpty(prefixes)) {
            builder.append(' ');
            builder.append(prefixes);
        }
        builder.append('>');
        if (notNullOrEmpty(xml)) {
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
