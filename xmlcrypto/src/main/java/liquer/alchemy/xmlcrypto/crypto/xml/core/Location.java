package liquer.alchemy.xmlcrypto.crypto.xml.core;

import static liquer.alchemy.xmlcrypto.support.StringSupport.isNullOrEmpty;

public class Location {

    private String reference;

    private ActionTypes action;

    public Location() {
        reference = "/*";
        action = ActionTypes.APPEND;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = isNullOrEmpty(reference) ? "/*" : reference;
    }

    public ActionTypes getAction() {
        return action;
    }

    public void setAction(ActionTypes action) {
        this.action = action == null ? ActionTypes.APPEND : action;
    }
}
