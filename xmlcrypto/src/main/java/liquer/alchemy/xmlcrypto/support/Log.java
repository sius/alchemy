package liquer.alchemy.xmlcrypto.support;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface Log<T> {

    public static final int LF = 10;

    default T println(String message) {
        return println(message, System.out);
    }

    default T println(String message, OutputStream out) {
        if (out == null || System.out == out) {
            System.out.println(message);
        } else {
            try (final OutputStream o = out) {
                o.write(message.getBytes(StandardCharsets.UTF_8));
                o.write(LF);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return (T)this;
    }
}
