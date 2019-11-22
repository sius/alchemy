package liquer.alchemy.xmlcrypto.crypto.xml.core;

import java.util.HashMap;
import java.util.Map;

public class SignatureOptions {

    public SignatureOptions() {
        attrs = new HashMap<>();
        existingPrefixes = new HashMap<>();
        location = new Location();
    }


    // Adds a prefix for the generated signature tags
    private String prefix;

    // A hash of attributes and values `attrName: value` to add to the signature root node
    private Map<String, String> attrs;

    private Location location;

    private Map<String, String> existingPrefixes;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Map<String, String> getAttrs() {
        return new HashMap<>(attrs);
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs =
                attrs == null
                        ? new HashMap<>()
                        : attrs;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, String> getExistingPrefixes() {
        return new HashMap<>(existingPrefixes);
    }

    public void setExistingPrefixes(Map<String, String> existingPrefixes) {
        this.existingPrefixes =
                existingPrefixes == null
                    ? new HashMap<>()
                    : new HashMap<>(existingPrefixes);
    }
}
