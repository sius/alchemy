package liquer.alchemy.crypto.xml.saml.core;

import liquer.alchemy.crypto.xml.saml.AudienceRestriction;
import liquer.alchemy.crypto.xml.core.NodeReader;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AudienceRestrictionImpl extends NodeReader implements AudienceRestriction {

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
