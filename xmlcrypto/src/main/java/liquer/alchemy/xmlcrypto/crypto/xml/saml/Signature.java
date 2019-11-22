package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.crypto.xml.KeyInfo;
import liquer.alchemy.xmlcrypto.crypto.xml.XmlReference;

import java.util.List;

public interface Signature {

    String getCanonicalizationAlgorithm();

    String getSignatureAlgorithm();

    String getSignatureValue();

    List<String> getTransforms();

    List<XmlReference> getReferences();

    XmlReference getAssertionReference();

    KeyInfo getKeyInfo();
}
