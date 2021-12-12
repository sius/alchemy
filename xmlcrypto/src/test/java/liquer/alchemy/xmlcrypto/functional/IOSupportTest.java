package liquer.alchemy.xmlcrypto.functional;

import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IOSupportTest {

    @Test
    void testToCharArray() throws IOException {

        String test = "test";
        char[] expected = test.toCharArray();
        char[] actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes()));
        assertArrayEquals(expected, actual);

        test = "äöüß";
        expected = test.toCharArray();
        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes("UTF-8")),"UTF-8" );
        assertArrayEquals(expected, actual);

        test = "äöüß";
        expected = test.toCharArray();
        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes("UTF-8")), "ASCII" );
        assertNotEquals(expected.length, actual.length);

//        test = "test";
//        expected = new char[] { 't', 'e' };
//        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes()), 2);
//        assertArrayEquals(expected, actual);
    }
}
