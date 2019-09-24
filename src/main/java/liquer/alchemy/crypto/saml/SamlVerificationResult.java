package liquer.alchemy.crypto.saml;

import liquer.alchemy.crypto.VerificationResult;

import java.util.List;

public interface SamlVerificationResult extends VerificationResult {

    List<String> getValidationErrors();

}
