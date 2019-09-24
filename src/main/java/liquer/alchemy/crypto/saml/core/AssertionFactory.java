package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.*;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import liquer.alchemy.crypto.xml.XPathSelector;
import liquer.alchemy.crypto.xml.XmlSigner;
import liquer.alchemy.crypto.xml.XmlUtil;
import liquer.alchemy.util.IOUtil;
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
public class AssertionFactory extends SafeNodeReader implements Assertion {

    public static Assertion of(InputStream in) throws IOException {
        return of(in, null);
    }

    public static Assertion of(InputStream in, NamespaceContext context) throws IOException {
        return of(IOUtil.toString(in), context);
    }

    public static Assertion of(String xml) {
        return of(xml, null);
    }

    public static Assertion of(String xml, NamespaceContext context) {
        return of(XmlUtil.toDocument(xml), context);
    }

    public static Assertion of(Document doc) {
        return of(doc, null);
    }

    public static Assertion of(Document doc, NamespaceContext namespaceContext) {
        return new AssertionFactory(doc, namespaceContext);
    }

    private final DateTime issueInstant;
    private final String issuer;
    private final Subject subject;
    private final Conditions conditions;
    private final List<AttributeStatement> attributeStatements;
    private final XmlSigner xmlSigner;
    private final Signature signature;

    private AssertionFactory(Document doc) {
        this(doc, new DefaultNamespaceContextMap());
    }

    private AssertionFactory(Document doc, NamespaceContext namespaceContext) {

        final NamespaceContext finalNamespaceContext = (namespaceContext == null)
            ? new DefaultNamespaceContextMap()
            : namespaceContext;
        final Element docElem = doc.getDocumentElement();
        final BiFunction<Node, String, Stream<Node>> select =
                XPathSelector.getSelectStreamClosure(finalNamespaceContext);

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

        xmlSigner = new XmlSigner();
        xmlSigner.setKeyInfoProvider(new EmbeddedKeyInfo());
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
    public SamlVerificationResult verifySignature(String xml) {

        final boolean validSignature = xmlSigner.verifySignature(xml);
        final List<String> validationErrors = xmlSigner.getValidationErrors();
        return new SamlVerificationResult() {

            @Override
            public boolean isValidSignature() { return validSignature; }

            @Override
            public boolean isValidToken() { return validationErrors.isEmpty(); }

            @Override
            public List<String> getValidationErrors() { return validationErrors; }
        };
     }
}
