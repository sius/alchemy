package liquer.alchemy.xmlcrypto.crypto.xml.core;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Emits xml without xml prolog
 */
public class FragmentXMLStreamWriter implements XMLStreamWriter {

    private final XMLStreamWriter w;

    public FragmentXMLStreamWriter(XMLStreamWriter writer) {
        w = writer;
    }

    @Override
    public void writeStartElement(String s) throws XMLStreamException {
        w.writeStartElement(s);
    }

    @Override
    public void writeStartElement(String s, String s1) throws XMLStreamException {
        w.writeStartElement(s, s1);
    }

    @Override
    public void writeStartElement(String s, String s1, String s2) throws XMLStreamException {
        w.writeStartElement(s, s1, s2);
    }

    @Override
    public void writeEmptyElement(String s, String s1) throws XMLStreamException {
        w.writeEmptyElement(s, s1);
    }

    @Override
    public void writeEmptyElement(String s, String s1, String s2) throws XMLStreamException {
        w.writeEmptyElement(s,s1,s2);
    }

    @Override
    public void writeEmptyElement(String s) throws XMLStreamException {
        w.writeEmptyElement(s);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        w.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        // avoid xml prolog
    }

    @Override
    public void close() throws XMLStreamException {
        w.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        w.flush();
    }

    @Override
    public void writeAttribute(String s, String s1) throws XMLStreamException {
        w.writeAttribute(s, s1);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2, String s3) throws XMLStreamException {
        w.writeAttribute(s, s1);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2) throws XMLStreamException {
        w.writeAttribute(s, s1, s2);
    }

    @Override
    public void writeNamespace(String s, String s1) throws XMLStreamException {
        w.writeNamespace(s, s1);
    }

    @Override
    public void writeDefaultNamespace(String s) throws XMLStreamException {
        w.writeDefaultNamespace(s);
    }

    @Override
    public void writeComment(String s) throws XMLStreamException {
        w.writeComment(s);
    }

    @Override
    public void writeProcessingInstruction(String s) throws XMLStreamException {
        w.writeProcessingInstruction(s);
    }

    @Override
    public void writeProcessingInstruction(String s, String s1) throws XMLStreamException {
        w.writeProcessingInstruction(s, s1);
    }

    @Override
    public void writeCData(String s) throws XMLStreamException {
        w.writeCData(s);
    }

    @Override
    public void writeDTD(String s) throws XMLStreamException {
        w.writeDTD(s);
    }

    @Override
    public void writeEntityRef(String s) throws XMLStreamException {
        w.writeEntityRef(s);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        // avoid xml prolog
    }

    @Override
    public void writeStartDocument(String s) throws XMLStreamException {
        // avoid xml prolog
    }

    @Override
    public void writeStartDocument(String s, String s1) throws XMLStreamException {
        // avoid xml prolog
    }

    @Override
    public void writeCharacters(String s) throws XMLStreamException {
        w.writeCharacters(s);
    }

    @Override
    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        w.writeCharacters(chars, i, i1);
    }

    @Override
    public String getPrefix(String s) throws XMLStreamException {
        return w.getPrefix(s);
    }

    @Override
    public void setPrefix(String s, String s1) throws XMLStreamException {
        w.setPrefix(s, s1);
    }

    @Override
    public void setDefaultNamespace(String s) throws XMLStreamException {
        w.setDefaultNamespace(s);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        w.setNamespaceContext(namespaceContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return w.getNamespaceContext();
    }

    @Override
    public Object getProperty(String s) throws IllegalArgumentException {
        return w.getProperty(s);
    }
}
