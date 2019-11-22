package liquer.alchemy.xmlcrypto.functional;

import liquer.alchemy.xmlcrypto.support.IOSupport;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class IOSupportTest {

    @Test
    public void testToCharArray() throws IOException {

        String test = "test";
        char[] expected = test.toCharArray();
        char[] actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes()));
        Assert.assertArrayEquals(expected, actual);

        test = "äöüß";
        expected = test.toCharArray();
        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes("UTF-8")),"UTF-8" );
        Assert.assertArrayEquals(expected, actual);

        test = "äöüß";
        expected = test.toCharArray();
        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes("UTF-8")), "ASCII" );
        Assert.assertNotEquals(expected.length, actual.length);

//        test = "test";
//        expected = new char[] { 't', 'e' };
//        actual = IOSupport.toCharArray(new ByteArrayInputStream(test.getBytes()), 2);
//        Assert.assertArrayEquals(expected, actual);
    }
}
