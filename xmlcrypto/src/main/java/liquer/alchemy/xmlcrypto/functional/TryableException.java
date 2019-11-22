package liquer.alchemy.xmlcrypto.functional;

public final class TryableException extends RuntimeException {

    TryableException(String message) {
        this(message, null);
    }
    TryableException(Throwable throwable) {
        this( throwable != null ? throwable.getMessage() : "", throwable);
    }
    TryableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
