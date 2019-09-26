package liquer.alchemy.crypto.xml.saml;

import java.util.List;

public interface Conditions {

    DateTime getNotBefore();

    DateTime getNotOnOrAfter();

    List<AudienceRestriction> getAudienceRestrictions();
}
