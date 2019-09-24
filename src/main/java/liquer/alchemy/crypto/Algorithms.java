package liquer.alchemy.crypto;

import liquer.alchemy.crypto.alg.HashAlgorithm;
import liquer.alchemy.crypto.alg.SignatureAlgorithm;
import liquer.alchemy.crypto.xml.c14n.CanonicalXml;
import liquer.alchemy.crypto.xml.c14n.Canonicalization;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {

    private static class SingletonHelper {

        private static final Map<java.lang.String, CanonicalXml> CANONICALIZATION_ALG_MAP;
        private static final Map<java.lang.String, SignatureAlgorithm> SIGNATURE_ALG_MAP;
        private static final Map<java.lang.String, HashAlgorithm> HASH_ALG_MAP;

        private static final Algorithms INSTANCE;

        static {
            CANONICALIZATION_ALG_MAP = new HashMap<>();
            for(Canonicalization alg : Canonicalization.values()) {
                CANONICALIZATION_ALG_MAP.put(alg.getIdentifier(), alg.getAlgorithm());
            }

            SIGNATURE_ALG_MAP = new HashMap<>();
            for(SignatureAlgorithm alg : SignatureAlgorithms.values()) {
                SIGNATURE_ALG_MAP.put(alg.getIdentifier(), alg);
            }

            HASH_ALG_MAP = new HashMap<>();
            for(HashAlgorithm alg : HashAlgorithms.values()) {
                HASH_ALG_MAP.put(alg.getIdentifier(), alg);
            }

            INSTANCE = new Algorithms(
                    CANONICALIZATION_ALG_MAP,
                    SIGNATURE_ALG_MAP,
                    HASH_ALG_MAP);
        }
    }
    public static Algorithms getInstance() {
        return Algorithms.SingletonHelper.INSTANCE;
    }

    private final Map<java.lang.String, CanonicalXml> canonicalizationAlgorithms;
    private final Map<java.lang.String, SignatureAlgorithm> signatureAlgorithms;
    private final Map<java.lang.String, HashAlgorithm> hashAlgorithms;

    private Algorithms(
            Map<java.lang.String, CanonicalXml> canonicalizationAlgorithms,
            Map<java.lang.String, SignatureAlgorithm> signatureAlgorithms,
            Map<java.lang.String, HashAlgorithm> hashAlgorithms) {
        this.canonicalizationAlgorithms = canonicalizationAlgorithms;
        this.signatureAlgorithms = signatureAlgorithms;
        this.hashAlgorithms = hashAlgorithms;
    }

    public Map<java.lang.String, CanonicalXml> getCanonicalizationAlgorithms() {
        return canonicalizationAlgorithms;
    }

    public Map<java.lang.String, SignatureAlgorithm> getSignatureAlgorithms() {
        return signatureAlgorithms;
    }

    public Map<java.lang.String, HashAlgorithm> getHashAlgorithms() {
        return hashAlgorithms;
    }
}
