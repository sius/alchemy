package liquer.alchemy.crypto.saml;

import java.util.List;

public interface Conditions {

    DateTime getNotBefore();

    DateTime getNotOnOrAfter();

    List<AudienceRestriction> getAudienceRestrictions();
}
