package liquer.alchemy.xmlcrypto.crypto.xml.saml.core;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

public class LSInputImpl implements LSInput {

    protected String publicId = null;
    protected String systemId = null;
    protected String baseSystemId = null;

    protected InputStream byteStream = null;
    protected Reader characterStream = null;
    protected String data = null;

    protected String encoding = null;

    protected boolean certifiedText = false;

    public LSInputImpl(String publicId,
                        String systemId,
                        String baseSystemId,
                        InputStream byteStream,
                        String encoding) {

        this.publicId = publicId;
        this.systemId = systemId;
        this.baseSystemId = baseSystemId;
        this.byteStream = byteStream;
        this.encoding = encoding;

    }
    @Override
    public Reader getCharacterStream() {
        return characterStream;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }

    @Override
    public InputStream getByteStream() {
        return byteStream;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    @Override
    public String getStringData() {
        return data;
    }

    @Override
    public void setStringData(String stringData) {
        this.data = stringData;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getBaseURI() {
        return baseSystemId;
    }

    @Override
    public void setBaseURI(String baseURI) {
        this.baseSystemId = baseURI;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean getCertifiedText() {
        return certifiedText;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
}
