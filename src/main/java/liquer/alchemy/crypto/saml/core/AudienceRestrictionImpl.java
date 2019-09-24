package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.AudienceRestriction;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AudienceRestrictionImpl extends SafeNodeReader implements AudienceRestriction {

    private final List<String> audiences;

    AudienceRestrictionImpl(Node node, BiFunction<Node, String, Stream<Node>> select) {

        audiences = select.apply(node,"./saml:Audience/text()")
            .map(this::readNodeValue)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getAudiences() {
        return Collections.unmodifiableList(audiences);
    }
}
