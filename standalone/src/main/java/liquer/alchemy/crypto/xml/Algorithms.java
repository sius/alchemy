package liquer.alchemy.crypto.xml;

import liquer.alchemy.crypto.Signature;
import liquer.alchemy.crypto.alg.HashAlgorithm;
import liquer.alchemy.crypto.alg.SignatureAlgorithm;
import liquer.alchemy.crypto.xml.c14n.CanonicalXml;
import liquer.alchemy.crypto.xml.c14n.Canonicalization;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {

    private static class SingletonHelper {

        private static final Map<String, CanonicalXml> CANONICALIZATION_ALG_MAP;
        private static final Map<String, SignatureAlgorithm> SIGNATURE_ALG_MAP;
        private static final Map<String, HashAlgorithm> HASH_ALG_MAP;

        private static final Algorithms INSTANCE;

        static {
            CANONICALIZATION_ALG_MAP = new HashMap<>();
            for(Canonicalization alg : Canonicalization.values()) {
                CANONICALIZATION_ALG_MAP.put(alg.getIdentifier(), alg.getAlgorithm());
            }

            SIGNATURE_ALG_MAP = new HashMap<>();
            for(SignatureAlgorithm alg : Signature.values()) {
                SIGNATURE_ALG_MAP.put(alg.getIdentifier(), alg);
            }

            HASH_ALG_MAP = new HashMap<>();
            for(HashAlgorithm alg : Hash.values()) {
                HASH_ALG_MAP.put(alg.getIdentifier(), alg);
            }

            INSTANCE = new Algorithms(
                    CANONICALIZATION_ALG_MAP,
                    SIGNATURE_ALG_MAP,
                    HASH_ALG_MAP);
        }
    }
    public static Algorithms getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private final Map<String, CanonicalXml> canonicalizationAlgorithms;
    private final Map<String, SignatureAlgorithm> signatureAlgorithms;
    private final Map<String, HashAlgorithm> hashAlgorithms;

    private Algorithms(
            Map<String, CanonicalXml> canonicalizationAlgorithms,
            Map<String, SignatureAlgorithm> signatureAlgorithms,
            Map<String, HashAlgorithm> hashAlgorithms) {
        this.canonicalizationAlgorithms = canonicalizationAlgorithms;
        this.signatureAlgorithms = signatureAlgorithms;
        this.hashAlgorithms = hashAlgorithms;
    }

    public Map<String, CanonicalXml> getCanonicalizationAlgorithms() {
        return canonicalizationAlgorithms;
    }

    public Map<String, SignatureAlgorithm> getSignatureAlgorithms() {
        return signatureAlgorithms;
    }

    public Map<String, HashAlgorithm> getHashAlgorithms() {
        return hashAlgorithms;
    }
}
