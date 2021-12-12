package liquer.alchemy.xmlcrypto.functional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TryTest {

    private static boolean getBoolean() throws Exception {
        throw new Exception("tryable test exception");
    }

    @Test
    void testTrySuccessfully() {
        final boolean expected = false;
        Try<Boolean> myTry = Try.of(() -> false);
        assertTrue(myTry.isSuccess());
        final boolean actual = myTry.orElseGet(true);
        assertEquals(expected, actual);
    }

    @Test
    void testTryOrElseGet() {
        final boolean expected = true;
        final boolean actual = Try.of(() -> getBoolean()).orElseGet(expected);
        assertEquals(expected, actual);
    }

    @Test
    void testTryOrElseThrow() {
        final boolean expected = true;
        try {
            Try<Boolean> myTry = Try.of(() -> getBoolean());
            assertTrue(myTry.isFailure());
            final boolean actual = myTry.orElseThrow();
        } catch (TryableException e) {
            assertEquals("tryable test exception", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    void testTryOrElseThrowWithCustomSupplier() {
        final boolean expected = true;
        try {
            final boolean actual = Try.of(() -> getBoolean())
                .orElseThrow(() -> new Exception("supplied exception"));
        } catch (TryableException e) {
            assertEquals("supplied exception", e.getMessage());
            return;
        }
        fail();
    }
}
