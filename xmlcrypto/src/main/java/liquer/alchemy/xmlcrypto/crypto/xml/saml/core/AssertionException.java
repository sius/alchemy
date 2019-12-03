package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

public final class AssertionException extends Exception {

    private static final String DEFAULT_MESSAGE = "Invalid Assertion";

    AssertionException() {
        this(DEFAULT_MESSAGE);
    }

    AssertionException(String message) {
        this(message == null ? DEFAULT_MESSAGE : message, null);
    }

    AssertionException(String message, Throwable throwable) {
        super(message == null ? DEFAULT_MESSAGE : message, throwable);
    }
}
