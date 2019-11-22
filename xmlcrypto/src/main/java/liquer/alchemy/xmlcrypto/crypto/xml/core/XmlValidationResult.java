package liquer.alchemy.xmlcrypto.crypto.xml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.ValidationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XmlValidationResult implements ValidationResult {

    private final boolean validSignature;
    private final List<String> errors;

    public XmlValidationResult(boolean validSignature, List<String> errors) {
        this.validSignature = validSignature;
        this.errors = (errors == null)
            ? new ArrayList<>()
            : new ArrayList<>(errors);
    }

    @Override
    public boolean isValidSignature() { return validSignature; }

    @Override
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
}
