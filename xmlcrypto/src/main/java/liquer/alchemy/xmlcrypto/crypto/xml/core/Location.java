package liquer.alchemy.xmlcrypto.crypto.xml.core;

import liquer.alchemy.xmlcrypto.support.StringSupport;

public class Location {

    private String reference;

    private ActionTypes action;

    public Location() {
        setReference("/*");
        setAction(ActionTypes.append);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = StringSupport.isNullOrEmpty(reference) ? "/*" : reference;
    }

    public ActionTypes getAction() {
        return action;
    }

    public void setAction(ActionTypes action) {
        this.action = action == null ? ActionTypes.append : action;
    }
}
