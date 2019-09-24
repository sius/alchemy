package liquer.alchemy.crypto.saml.core;

import liquer.alchemy.crypto.saml.AudienceRestriction;
import liquer.alchemy.crypto.saml.Conditions;
import liquer.alchemy.crypto.saml.DateTime;
import liquer.alchemy.crypto.xml.SafeNodeReader;
import org.w3c.dom.Node;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConditionsImpl extends SafeNodeReader implements Conditions {

    private final DateTime notBefore;
    private final DateTime notOnOrAfter;
    private final List<AudienceRestriction> audienceRestrictions;

    ConditionsImpl(Node node, BiFunction<Node, String, Stream<Node>> select) {
        notBefore = new DateTimeImpl(readNamedItem(node, "NotBefore"));
        notOnOrAfter = new DateTimeImpl(readNamedItem(node, "NotOnOrAfter"));
        audienceRestrictions = select.apply(node, "./saml:AudienceRestriction")
            .map(n -> new AudienceRestrictionImpl(n, select)).collect(Collectors.toList());
    }

    @Override
    public DateTime getNotBefore() { return notBefore; }

    @Override
    public DateTime getNotOnOrAfter() { return notOnOrAfter; }

    @Override
    public List<AudienceRestriction> getAudienceRestrictions() {
        return audienceRestrictions;
    }
}
