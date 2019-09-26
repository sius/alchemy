package liquer.alchemy.crypto.xml.saml;

import java.util.List;

public interface SamlValidationResult {

    List<String> getValidationErrors();

    default boolean isValidSignature() { return false; }

    default boolean isValidToken() { return false; }
}
