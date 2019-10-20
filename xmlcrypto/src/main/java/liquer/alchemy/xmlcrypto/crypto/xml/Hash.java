package liquer.alchemy.xmlcrypto.crypto.xml;

import liquer.alchemy.xmlcrypto.crypto.Identifier;
import liquer.alchemy.xmlcrypto.crypto.alg.HashAlgorithm;

public enum Hash implements HashAlgorithm {

    SHA1(Identifier.SHA1, "SHA-1"),
    SHA224(Identifier.SHA224, "SHA-224"),
    SHA256(Identifier.SHA256, "SHA-256"),
    SHA384(Identifier.SHA384, "SHA-384"),
    SHA512(Identifier.SHA512, "SHA-512")

    ;

    private final String identifier;
    private final String algorithm;

    Hash(String identifier, String algorithm) {
        this.identifier = identifier;
        this.algorithm = algorithm;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return algorithm;
    }

}
