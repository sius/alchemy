package liquer.alchemy.crypto.alg;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.crypto.opt.HashOptions;

import java.util.function.BiFunction;

public interface HashAlgorithm extends Algorithm, BiFunction<String, HashOptions, String> {

    default String hash(String value, HashOptions options) {
        return apply(value, options);
    }

    default String apply(String value, HashOptions options) {
        options = options == null
                ? new HashOptions()
                : options;
        return CryptoLimericks.hash(value, getAlgorithm(), options.isZeroTerminated(), options.getCharset(), options.getEncoder());
    }
}
