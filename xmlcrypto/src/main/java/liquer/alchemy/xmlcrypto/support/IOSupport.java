package liquer.alchemy.xmlcrypto.support;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class IOSupport {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private IOSupport() { }

    public static char[] toCharArray(InputStream is) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream is, String encoding) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    public static String toString(URL url) throws IOException {
        return toString(url.getFile());
    }

    public static String toString(String filePath) throws IOException {
        return toString(new File(filePath));
    }

    public static String toString(File f) throws IOException {
        return toString(new FileInputStream(f));
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static String toString(Reader input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        return (int) count;
    }

    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input, StandardCharsets.UTF_8);
        copy(in, output);
    }

    public static void copy(InputStream input, Writer output, String charset) throws IOException {
        if (charset == null) {
            copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, Charset.forName(charset));
            copy(in, output);
        }
    }

    /**
     * @param input an InputStream
     * @return an empty byte[0] array if input is null
     * @throws IOException IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        if (input == null) {
            return new byte[0];
        }
        byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        while ((read = input.read(buff, 0, buff.length)) > -1) {
            dest.write(buff, 0, read);
        }
        return dest.toByteArray();
    }
}
