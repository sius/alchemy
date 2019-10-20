package liquer.alchemy.xmlcrypto.crypto.opt;

import liquer.alchemy.xmlcrypto.support.BaseN;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class EncoderOptions {

    private Charset charset;
    private Function<byte[], String> encoder;

    public EncoderOptions() {
        charset = StandardCharsets.UTF_8;
        encoder = BaseN::base64Encode;
    }

    public Charset getCharset() {
        return charset;
    }

    public Function<byte[], String> getEncoder() {
        return encoder;
    }

    public void setEncoder(Function<byte[], String> encoder) {
        this.encoder = encoder;
    }
}
