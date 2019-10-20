package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

import liquer.alchemy.xmlcrypto.crypto.Identifier;

import java.lang.reflect.InvocationTargetException;

public enum Canonicalization {

    CANONICAL_XML_1_0_OMIT_COMMENTS(Identifier.CANONICAL_XML_1_0_OMIT_COMMENTS, CanonicalXml_1_0_OmitComments.class),
    CANONICAL_XML_1_0_WITH_COMMENTS(Identifier.CANONICAL_XML_1_0_WITH_COMMENTS, CanonicalXml_1_0_WithComments.class),
    CANONICAL_XML_1_1_OMIT_COMMENTS(Identifier.CANONICAL_XML_1_1_OMIT_COMMENTS, CanonicalXml_1_1_OmitComments.class),
    CANONICAL_XML_1_1_WITH_COMMENTS(Identifier.CANONICAL_XML_1_1_WITH_COMMENTS, CanonicalXml_1_1_WithComments.class),
    EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_OMIT_COMMENTS, ExclusiveCanonicalXml_1_0_OmitComments.class),
    EXCLUSIVE_CANONICAL_XML_1_0_WITH_COMMENTS(Identifier.EXCLUSIVE_CANONICAL_XML_1_0_WITH_COMMENTS, ExclusiveCanonicalXml_1_0_WithComments.class),
    EnvelopedSignature(Identifier.ENVELOPED_SIGNATURE, EnvelopedSignature.class)

    ;

    private final Class<?> algorithm;
    private final String identifier;

    Canonicalization(String identifier, Class<? extends CanonicalXml> algorithm) {
        this.identifier = identifier;
        this.algorithm = algorithm;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public CanonicalXml getAlgorithm() {
        final Object b;
        try {
            b = algorithm.getDeclaredConstructor().newInstance();
            return (b instanceof CanonicalXml) ? (CanonicalXml)b : null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
           throw new RuntimeException(e);
        }
    }
}

