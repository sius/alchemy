package liquer.alchemy.crypto.xml.saml.core;

import liquer.alchemy.crypto.xml.core.NodeReader;
import liquer.alchemy.crypto.xml.core.EOL;
import liquer.alchemy.crypto.xml.saml.*;
import liquer.alchemy.crypto.xml.*;
import liquer.alchemy.alembic.IOSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple Assertion Implementation
 */
public class AssertionFactory extends NodeReader implements Assertion {

    public static Assertion of(InputStream in) throws IOException {
        return of(in, null);
    }

    public static Assertion of(InputStream in, NamespaceContext context) throws IOException {
        return of(IOSupport.toString(in), context);
    }

    public static Assertion of(String xml) {
        return of(xml, new DefaultNamespaceContextMap());
    }

    public static Assertion of(String xml, NamespaceContext context) {
        EOL eol = (xml != null && xml.contains("\r\n"))
                ? EOL.CRLF
                : EOL.LF;
        return of(XmlSupport.toDocument(xml), context, eol);
    }

    public static Assertion of(Document doc, EOL eol) {
        return of(doc, new DefaultNamespaceContextMap(), eol);
    }

    public static Assertion of(Document doc, NamespaceContext namespaceContext, EOL eol) {
        return new AssertionFactory(doc, namespaceContext, eol);
    }

    private final DateTime issueInstant;
    private final String issuer;
    private final Subject subject;
    private final Conditions conditions;
    private final List<AttributeStatement> attributeStatements;
    private final XmlSigner xmlSigner;
    private final Signature signature;

    private AssertionFactory(Document doc, NamespaceContext namespaceContext, EOL eol) {

        final NamespaceContext finalNamespaceContext = (namespaceContext == null)
            ? new DefaultNamespaceContextMap()
            : namespaceContext;

        final Element docElem = doc.getDocumentElement();
        final BiFunction<Node, String, Stream<Node>> select =
                XPathSupport.getSelectStreamClosure(finalNamespaceContext);

        issueInstant = new DateTimeImpl(readNamedItem(docElem, "IssueInstant"));

        issuer = select.apply(docElem, "/*/saml:Issuer/text()")
                .findFirst()
                .orElse(doc.createTextNode(null))
                .getNodeValue();

        subject = new SubjectImpl(select.apply(docElem, "/*/saml:Subject")
                .findFirst()
                .orElse(doc.createElement("saml:Subject")), select);

        conditions = new ConditionsImpl(select.apply(docElem, "/*/saml:Conditions")
                .findFirst()
                .orElse(doc.createElement("saml:Conditions")), select);

        attributeStatements = select.apply(docElem,"/*/saml:AttributeStatement")
                .map(n -> new AttributeStatementImpl(n, select))
                .collect(Collectors.toList());

        final Node signatureNode = select.apply(docElem, "/*/*[local-name(.)='Signature' and namespace-uri(.)='http://www.w3.org/2000/09/xmldsig#']")
                .findFirst()
                .orElseThrow( () -> new RuntimeException("No Signature fount"));

        XmlSignerOptions options = new XmlSignerOptions();
        options.setNamespaceContext(namespaceContext);
        options.setEol(eol);

        xmlSigner = new XmlSigner(options);
        xmlSigner.setKeyInfoProvider(new EnvelopedKeyInfo());
        xmlSigner.loadSignature(signatureNode);
        signature = new SignatureImpl(xmlSigner.getKeyInfoProvider());
    }

    @Override
    public DateTime getIssueInstant() {
        return issueInstant;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public Subject getSubject() { return subject; }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public List<AttributeStatement> getAttributeStatements() {
        return attributeStatements;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public XmlSigner getXmlSigner() {
        return xmlSigner;
    }

    @Override
    public ValidationResult validateSignature(String xml) {
        return xmlSigner.validateSignature(xml);
     }
}
