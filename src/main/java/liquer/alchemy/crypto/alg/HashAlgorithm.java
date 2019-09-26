package liquer.alchemy.crypto.alg;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.xml.ext.HashOptions;

public interface HashAlgorithm extends Algorithm {

    default String hash(String value, HashOptions options) {

        options = options == null
                ? new HashOptions()
                : options;
        return CryptoLimericks.hash(value, getName(), options.isZeroTerminated(), options.getCharset(), options.getEncoder());
    }
}
