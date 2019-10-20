package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.xml.*;
import liquer.alchemy.xmlcrypto.crypto.xml.core.NodeReader;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Signature;
import liquer.alchemy.xmlcrypto.functional.Func2;
import liquer.alchemy.xmlcrypto.support.StringSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class XPathSignatureReader extends NodeReader implements Signature {

    private static final Logger LOG = LogManager.getLogger(XPathSignatureReader.class);

    private static final String XPATH_EXPR_FORMAT_ALG = ".//*[local-name(.)='%1$s']/@Algorithm";
    private static final String XPATH_EXPR_C14N_ALG = String.format(XPATH_EXPR_FORMAT_ALG, "CanonicalizationMethod");
    private static final String XPATH_EXPR_SIG_ALG = String.format(XPATH_EXPR_FORMAT_ALG, "SignatureMethod");
    private static final String XPATH_EXPR_SIGNED_INFO_REF = ".//*[local-name(.)='SignedInfo']/*[local-name(.)='Reference']";
    private static final String XPATH_EXPR_SIGNATURE_VALUE_TEXT = ".//*[local-name(.)='SignatureValue']/text()";
    private static final String XPATH_EXPR_KEY_INFO = ".//*[local-name(.)='KeyInfo']";

    private final List<String> implicitTransforms;
    private final String canonicalizationAlgorithm;
    private final String signatureAlgorithm;
    private final List<XmlReference> references;
    private final List<Node> keyInfo;
    private final String signatureValue;
    private final List<String> transforms;

    XPathSignatureReader(Node signatureNode, XmlSignerOptions options, NamespaceContext context) {

        Func2<Node, String, NodeList> select = XPathSupport.getSelectNodeListClosure(context);
        transforms = new ArrayList<>();
        implicitTransforms = options.getImplicitTransforms();
        canonicalizationAlgorithm =
            readFirst(select.apply(signatureNode, XPATH_EXPR_C14N_ALG))
                .orElseThrow(() -> new RuntimeException("Cannot find Canonicalization/@Algorithm element"))
                .getNodeValue();

        signatureAlgorithm =
                readFirst(select.apply(signatureNode, XPATH_EXPR_SIG_ALG))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid argument signatureNode"))
                        .getNodeValue();

        references = mapNodeList(select.apply(signatureNode, XPATH_EXPR_SIGNED_INFO_REF), this::newXmlReference);

        if (references.isEmpty()) {
            throw new IllegalStateException("Cannot find any Reference elements");
        }

        signatureValue = readFirst(select.apply(signatureNode, XPATH_EXPR_SIGNATURE_VALUE_TEXT))
                .orElseThrow(() -> new RuntimeException("Cannot find any SignatureValue"))
                .getTextContent();

        keyInfo = mapNodeList(select.apply(signatureNode, XPATH_EXPR_KEY_INFO), n->n);
    }


    /*
     * Load the reference xml node to a model
     */
    private XmlReference newXmlReference(Node referenceNode) {
        NodeList nodes = XPathSupport.findNodeChildren(referenceNode, "DigestMethod");
        if (nodes.getLength() == 0) {
            throw new IllegalStateException(
                    "Cannot find DigestMethod node in reference " + XmlSupport.stringify(referenceNode));
        }
        Node digestAlgorithmNode = nodes.item(0);
        Node attr = XPathSupport.findAttribute(digestAlgorithmNode, "Algorithm", null);
        if (attr == null) {
            throw new IllegalStateException(
                    "Cannot find Algorithm attribute in node " + XmlSupport.stringify(digestAlgorithmNode));
        }
        String digestAlgorithm = attr.getNodeValue();

        NodeList digestValueNodes = XPathSupport.findNodeChildren(referenceNode, "DigestValue");
        if (digestValueNodes.getLength() == 0) {
            throw new IllegalStateException(
                    "Cannot find DigestValue node in reference " + XmlSupport.stringify(referenceNode));
        }
        Node firstDigestValueNode = digestValueNodes.item(0);
        if (!firstDigestValueNode.hasChildNodes()
                || StringSupport.isNullOrEmpty(firstDigestValueNode.getFirstChild().getTextContent())) {
            throw new IllegalStateException(
                    "Cannot find the value of DigestValue in " + XmlSupport.stringify(firstDigestValueNode));
        }

        String digestValue =  firstDigestValueNode.getFirstChild().getTextContent();

        List<String> inclusiveNamespacesPrefixList = new ArrayList<>();

        NodeList transformsNodes = XPathSupport.findNodeChildren(referenceNode, "Transforms");

        if (transformsNodes.getLength() > 0) {
            Node transformsNode = transformsNodes.item(0);
            NodeList transformsAll = XPathSupport.findNodeChildren(transformsNode, "Transform");
            Node trans = null;
            for (int i = 0; i < transformsAll.getLength(); i++) {
                trans = transformsAll.item(i);
                Node algorithmAttr = XPathSupport.findAttribute(trans, "Algorithm", null);
                if (algorithmAttr != null) {
                    transforms.add(algorithmAttr.getNodeValue());
                }

            }

            NodeList inclusiveNamespaces = XPathSupport.findNodeChildren(trans, "InclusiveNamespaces");
            if (inclusiveNamespaces.getLength() > 0) {
                //Should really only be one prefix list, but maybe there's some circumstances where more than one to lets handle it
                for (int i = 0; i < inclusiveNamespaces.getLength(); i++) {
                    Node prefixListAttr = inclusiveNamespaces.item(i).getAttributes().getNamedItem("PrefixList");
                    inclusiveNamespacesPrefixList.add(prefixListAttr.getNodeValue());
                }
            }
        }

        boolean hasImplicitTransforms = (this.implicitTransforms != null && !this.implicitTransforms.isEmpty());
        if(hasImplicitTransforms){
            this.implicitTransforms.addAll(transforms);
        }

        /*
         * DigestMethods take an octet stream rather than a node set.
         * If the output of the last transform is a node set,
         * we need to canonicalize the node set to an octet stream
         * using non-exclusive canonicalization.
         * If there are no transforms,
         * we need to canonicalize because URI de-referencing for a same-document reference
         * will return a node-set.
         * See:
         * https://www.w3.org/TR/xmldsig-core1/#sec-DigestMethod
         * https://www.w3.org/TR/xmldsig-core1/#sec-ReferenceProcessingModel
         * https://www.w3.org/TR/xmldsig-core1/#sec-Same-Document
         */
        if (transforms.isEmpty() || Identifier.ENVELOPED_SIGNATURE.equals(transforms.get(transforms.size()- 1))) {
            transforms.add(Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS);
        }

        final String uriNodeValue = readNodeValue(XPathSupport.findAttribute(referenceNode, "URI"));


        final XmlReference ref = new XmlReference();
        ref.setXpathExpression(null);
        ref.setTransforms(transforms);
        ref.setDigestAlgorithm(digestAlgorithm);
        ref.setUri(uriNodeValue);
        ref.setDigestValue(digestValue);
        ref.setInclusiveNamespacesPrefixList(inclusiveNamespacesPrefixList);
        ref.setEmptyUri(false);

        return ref;
    }


    @Override
    public String getCanonicalizationAlgorithm() {
        return canonicalizationAlgorithm;
    }

    @Override
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    @Override
    public String getSignatureValue() {
        return signatureValue;
    }

    @Override
    public List<String> getTransforms() {
        return Collections.unmodifiableList(transforms);
    }

    @Override
    public List<XmlReference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    @Override
    public XmlReference getAssertionReference() {
        return null;
    }

    @Override
    public KeyInfo getKeyInfo() {
        return new EnvelopedKeyInfo();
    }



}
