package liquer.alchemy.athanor.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * An encoding safe Json writer to use with HTTP response streams
 */
public class JsonWriter extends Writer {

	private static final Logger LOG = LogManager.getLogger(JsonWriter.class);
	protected static final int DEFAULT_CHAR_BUFFER_SIZE = 8192;

	private String charsetName;
	private Writer out;

	public JsonWriter(OutputStream out) {
		this(out, Json.Singleton.CONFIG.getCharset(), DEFAULT_CHAR_BUFFER_SIZE);
	}
	public JsonWriter(OutputStream out, int bufferSize) {
		this(out, Json.Singleton.CONFIG.getCharset() , bufferSize);
	}
	public JsonWriter(OutputStream out, Charset charset) {
		this(out, charset, DEFAULT_CHAR_BUFFER_SIZE);
	}
	public JsonWriter(OutputStream out, Charset charset, int bufferSize) {
		Charset chs = charset == null ? Json.Singleton.CONFIG.getCharset() : charset;
		this.charsetName = chs.name();
		this.out = new BufferedWriter(new OutputStreamWriter(out, chs), Math.max(bufferSize, DEFAULT_CHAR_BUFFER_SIZE));
	}
	public JsonWriter(OutputStream out, String charsetName) {
		this(out, Charset.forName(charsetName), DEFAULT_CHAR_BUFFER_SIZE);
	}
	public JsonWriter(OutputStream out, String charsetName, int bufferSize) {
		this(out, Charset.forName(charsetName), bufferSize);
	}
	public String charsetName() {
		return charsetName;
	}

	@Override
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	@Override
	public void write(char[] cBuf, int off, int len) {
		try {
			out.write(cBuf, off, len);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	public void print(String s) {
		if (s == null) return;
		char[] cBuf = s.toCharArray();
		write(cBuf, 0, cBuf.length);
	}
	public void name(String key) { print("\"" + key + "\":"); }
	public void value(Json value) { value.print(this); }
	public void namedValue(String key, Json value) { name(key); value(value); }
	public void beginObj() { print("{"); }
    public void endObj() { print("}"); }
    public void beginSeq() { print("["); }
    public void endSeq() { print("]"); }
    public void comma() { print(","); }
    public void beginPadded(String callback) { print(callback + "("); }
    public void endPadded() { print(")"); }

    /**
     * Wraps the Json value into a Json Object
     * @param name property name
     * @param value property value
     */
    public void normalize(String name, Json value) {
    	if (name != null) {
	    	beginObj();
	    	name(name);
	    	value(value);
	    	endObj();
    	} else {
    		value(value);
    	}
    	flush();
    }
    public void pad(String callback, Json value) {
    	beginPadded(callback);
    	value(value);
    	endPadded();
    	flush();
    }
    public void normalizePad(String callback, String name, Json value) {
    	beginPadded(callback);
    	normalize(name, value);
    	endPadded();
    	flush();
    }
}
