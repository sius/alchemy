package liquer.alchemy.xmlcrypto.support;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface Log {

    default Log println(String message) {
        return println(message, System.out);
    }

    default Log println(String message, OutputStream out) {
        if (out == null || System.out == out) {
            System.out.println(message);
        } else {
            try (final OutputStream o = out) {
                o.write(message.getBytes(StandardCharsets.UTF_8));
                o.write(10);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    default void log(String message) {
        println(message);
    }

    default void log(String message, OutputStream out) {
        println(message, out);
    }
}
