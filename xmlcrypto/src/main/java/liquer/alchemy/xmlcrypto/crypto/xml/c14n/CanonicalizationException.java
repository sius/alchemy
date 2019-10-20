package liquer.alchemy.xmlcrypto.crypto.xml.c14n;

public class CanonicalizationException extends Exception {

    public CanonicalizationException(String message) {
        this(message, null);
    }
    public CanonicalizationException(Throwable throwable) {
        this( throwable != null ? throwable.getMessage() : "Cannot apply algorithm", throwable);
    }
    public CanonicalizationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
