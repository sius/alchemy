package liquer.alchemy.crypto.xml.saml.core;

import liquer.alchemy.crypto.xml.KeyInfo;
import liquer.alchemy.crypto.xml.saml.Signature;

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
