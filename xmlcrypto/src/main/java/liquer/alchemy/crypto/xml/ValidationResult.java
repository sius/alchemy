package liquer.alchemy.crypto.xml;

import java.util.List;

public interface ValidationResult {

    List<String> getValidationErrors();

    default boolean isValidSignature() { return false; }

    default boolean isValidToken() {
        final List<String> result = getValidationErrors();
        return (result == null || result.isEmpty());
    }
}
