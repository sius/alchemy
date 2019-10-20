package liquer.alchemy.xmlcrypto.crypto.alg;

import liquer.alchemy.xmlcrypto.crypto.CryptoSupport;
import liquer.alchemy.xmlcrypto.crypto.opt.HashOptions;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface HashAlgorithm extends Algorithm {

    default String hash(String value, HashOptions options) throws NoSuchProviderException, NoSuchAlgorithmException {

        options = options == null
                ? new HashOptions()
                : options;
        return CryptoSupport.hash(value, getName(), options.isZeroTerminated(), options.getCharset(), options.getEncoder());
    }
}
