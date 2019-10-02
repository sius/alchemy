# xmlcrypto Library

## Generate JAXB Assertion model

xjc \
-disableXmlSecurity \
-b saml-2.0-os/binding.xml \
-d src/main/java \
-p liquer.alchemy.crypto.xml.saml.jaxb.model \
saml-2.0-os/saml-schema-assertion-2.0.xsd

xjc \
-disableXmlSecurity \
-Xpropertyaccessors \
-b saml-2.0-os/binding.xml \
-d src/main/java \
-p liquer.alchemy.crypto.xml.saml.jaxb.model \
saml-2.0-os/saml-schema-assertion-2.0.xsd
