package liquer.alchemy.crypto.xml.core;

import java.io.StringWriter;

import static liquer.alchemy.crypto.xml.core.EOL.DEFAULT;
import static liquer.alchemy.crypto.xml.core.EOL.LF;

/**
 * An extended StringWriter that ensures the required End-of-Line characters
 **/
public final class EOLStringWriter extends StringWriter {

    private final String current;
    private final String replacement;

    public EOLStringWriter() {
        this(LF);
    }

    public EOLStringWriter(EOL eol) {
        final EOL finalEol = (eol == null) ? DEFAULT : eol;
        switch (finalEol) {
            case LF:
                current = "\r\n";
                replacement = "\n";
                break;
            case CRLF:
                current = "\n";
                replacement = "\r\n";
                break;
            default:
                current = null;
                replacement = null;
                break;
        }
    }

    @Override
    public String toString() {
        return (current == null)
            ? super.toString()
            : super.toString().replaceAll(current, replacement);
    }
}
