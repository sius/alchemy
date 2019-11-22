package liquer.alchemy.xmlcrypto.crypto.xml.saml;

import liquer.alchemy.xmlcrypto.support.BaseN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class Base64GZipped {

    private final String value;
    private final String token;

    public static String of(String value) {
        return new Base64GZipped(value).toString();
    }

    public Base64GZipped(String value) {
        this.value = value;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (GZIPOutputStream gout = new GZIPOutputStream(bout)) {
            gout.write(this.value.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.token = BaseN.base64Encode(bout.toByteArray());
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return token;
    }
}
