package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import liquer.alchemy.xmlcrypto.crypto.xml.saml.AudienceRestriction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AudienceRestrictionImpl implements AudienceRestriction {

    private final List<String> audiences;

    AudienceRestrictionImpl() {
        audiences = new ArrayList<>();
    }

    @Override
    public List<String> getAudiences() {
        return Collections.unmodifiableList(audiences);
    }

    void addAudience(String audience) {
        audiences.add(audience);
    }
}
