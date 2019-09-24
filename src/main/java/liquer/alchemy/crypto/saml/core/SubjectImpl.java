package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.Subject;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import org.w3c.dom.Node;

import java.util.function.BiFunction;
import java.util.stream.Stream;

class SubjectImpl extends SafeNodeReader implements Subject {

    private final String confirmationMethod;

    SubjectImpl(Node node, BiFunction<Node, String, Stream<Node>> select) {

        confirmationMethod = readNodeValue(
            select.apply(node,"./saml:SubjectConfirmation/@Method").findFirst());

    }

    @Override
    public String getConfirmationMethod() {
        return confirmationMethod;
    }
}
