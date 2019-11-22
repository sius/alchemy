package liquer.alchemy.xmlcrypto.crypto.alg;

import liquer.alchemy.xmlcrypto.crypto.CryptoSupport;
import liquer.alchemy.xmlcrypto.crypto.opt.HashOptions;

public interface HashAlgorithm extends Algorithm {

    default String hash(String value, HashOptions options) {

        final HashOptions hashOptions = options == null
                ? new HashOptions()
                : options;

        return CryptoSupport.hashHex(
            value,
            getName(),
            hashOptions.isZeroTerminated(),
            hashOptions.getCharset(),
            hashOptions.getEncoder());

    }
}
