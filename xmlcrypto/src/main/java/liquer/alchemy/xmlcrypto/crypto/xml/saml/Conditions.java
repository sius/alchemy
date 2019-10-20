package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import java.util.Calendar;
import java.util.List;

public interface Conditions {

    Calendar getNotBefore();

    Calendar getNotOnOrAfter();

    List<AudienceRestriction> getAudienceRestrictions();
}
