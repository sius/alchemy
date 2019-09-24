package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.KeyInfo;
import liquer.alchemy.crypto.saml.Signature;

class SignatureImpl implements Signature {

    private final KeyInfo keyInfo;

    SignatureImpl(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }

    @Override
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }
}
