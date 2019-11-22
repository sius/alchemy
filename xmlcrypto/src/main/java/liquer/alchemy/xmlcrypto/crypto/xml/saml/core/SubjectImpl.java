package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.saml.Subject;

class SubjectImpl implements Subject {

    private final String confirmationMethod;

    SubjectImpl(String confirmationMethod) {

        this.confirmationMethod = confirmationMethod;

    }

    @Override
    public String getConfirmationMethod() {
        return confirmationMethod;
    }
}
