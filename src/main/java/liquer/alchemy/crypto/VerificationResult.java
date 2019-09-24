package liquer.alchemy.crypto;

public interface VerificationResult {

    default boolean isValidSignature() { return false; }

    default boolean isValidToken() { return false; }

}
