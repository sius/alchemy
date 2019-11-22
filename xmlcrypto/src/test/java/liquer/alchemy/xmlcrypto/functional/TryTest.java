package liquer.alchemy.xmlcrypto.functional;

import org.junit.Assert;
import org.junit.Test;

public class TryTest {

    private static boolean getBoolean() throws Exception {
        throw new Exception("tryable test exception");
    }

    @Test
    public void testTrySuccessfully() {
        final boolean expected = false;
        Try<Boolean> myTry = Try.of(() -> false);
        Assert.assertTrue(myTry.isSuccess());
        final boolean actual = myTry.orElseGet(true);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTryOrElseGet() {
        final boolean expected = true;
        final boolean actual = Try.of(() -> getBoolean()).orElseGet(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTryOrElseThrow() {
        final boolean expected = true;
        try {
            Try<Boolean> myTry = Try.of(() -> getBoolean());
            Assert.assertTrue(myTry.isFailure());
            final boolean actual = myTry.orElseThrow();
        } catch (TryableException e) {
            Assert.assertEquals("tryable test exception", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testTryOrElseThrowWithCustomSupplier() {
        final boolean expected = true;
        try {
            final boolean actual = Try.of(() -> getBoolean())
                .orElseThrow(() -> new Exception("supplied exception"));
        } catch (TryableException e) {
            Assert.assertEquals("supplied exception", e.getMessage());
            return;
        }
        Assert.fail();
    }
}
