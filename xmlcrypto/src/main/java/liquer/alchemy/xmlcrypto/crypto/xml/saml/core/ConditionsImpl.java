package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.saml.AudienceRestriction;
import liquer.alchemy.xmlcrypto.crypto.xml.saml.Conditions;

import java.util.*;

class ConditionsImpl implements Conditions {

    private final Calendar notBefore;
    private final Calendar notOnOrAfter;
    private final List<AudienceRestriction> audienceRestrictions;

    ConditionsImpl(GregorianCalendar notBefore,
                   GregorianCalendar notOnOrAfter,
                   List<AudienceRestriction> audienceRestrictions) {

        this.notBefore = notBefore;
        this.notOnOrAfter = notOnOrAfter;
        this.audienceRestrictions = audienceRestrictions == null
            ? new ArrayList<>()
            : new ArrayList<>(audienceRestrictions);
    }

    @Override
    public Calendar getNotBefore() { return notBefore; }

    @Override
    public Calendar getNotOnOrAfter() { return notOnOrAfter; }

    @Override
    public List<AudienceRestriction> getAudienceRestrictions() {
        return Collections.unmodifiableList(audienceRestrictions);
    }
}
