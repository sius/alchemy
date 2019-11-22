package liquer.alchemy.xmlcrypto.crypto;

public class CryptoException extends RuntimeException {

    public CryptoException() {
        super();
    }
    public CryptoException(Throwable t) {
        super(t);
    }
    public CryptoException(String m) {
        super(m, null);
    }
    public CryptoException(String m, Throwable t) {
        super(m, t);
    }
}
