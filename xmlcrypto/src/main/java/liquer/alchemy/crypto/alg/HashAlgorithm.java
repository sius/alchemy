package liquer.alchemy.crypto.alg;

import liquer.alchemy.crypto.CryptoSupport;
import liquer.alchemy.crypto.opt.HashOptions;

public interface HashAlgorithm extends Algorithm {

    default String hash(String value, HashOptions options) {

        options = options == null
                ? new HashOptions()
                : options;
        return CryptoSupport.hash(value, getName(), options.isZeroTerminated(), options.getCharset(), options.getEncoder());
    }
}
