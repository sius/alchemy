package liquer.alchemy.xmlcrypto.crypto.xml;

public class XmlSignerException extends RuntimeException {


    public XmlSignerException() {
        super();
    }

    public XmlSignerException(Throwable t) {
        super(t);
    }

    public XmlSignerException(String m) {
        super(m, null);
    }
    public XmlSignerException(String m, Throwable t) {
        super(m, t);
    }
}
