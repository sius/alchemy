package liquer.alchemy.xmlcrypto.crypto.xml;

import java.util.ArrayList;
import java.util.List;

public interface ValidationResult {

    default List<String> getErrors() { return new ArrayList<>(); }

    default boolean isValidSignature() { return false; }

    default boolean isValidToken() {
        final List<String> result = getErrors();
        return (result == null || result.isEmpty());
    }
}
