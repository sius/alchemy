package liquer.alchemy.alembic;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestInputStream extends FilterInputStream {

	
	private MessageDigest messageDigest;
	private byte[] digest = null;
	/**
	 * Returns a Filter InputStream that build a MD5 Message digest while reading the InputStream
	 * @param in the InputStream
	 * @throws NoSuchAlgorithmException algorithm not found
	 */
	public DigestInputStream(InputStream in) throws NoSuchAlgorithmException {
		this(in, MessageDigest.getInstance("MD5"));
	}
	/**
	 * Returns a Filter InputStream that build the specified Message digest while reading the InputStream
	 * @param in he InputStream
	 * @param messageDigest the MessageDigest
	 */
	public DigestInputStream(InputStream in, MessageDigest messageDigest) {
		super(in);
		this.messageDigest  = messageDigest;
	}
	
	public byte[] getDigest() {
		return digest == null
			? new byte[0]
			: digest;
	}
	@Override public int read() throws IOException {
		int _t = super.read();
		if (_t > -1) {
			messageDigest.update((byte)_t);
		} else {
			if (digest == null) {
				digest = messageDigest.digest();
			}
		}
		return _t;
	}
	@Override 
	public int read(byte[] b) throws IOException {
		int _t = super.read(b);
		if (_t > -1) {
			messageDigest.update(b, 0, _t);
		} else {
			if (digest == null) {
				digest = messageDigest.digest();
			}
		}
		return _t;
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int _t = super.read(b, off, len);
		if (_t > -1) {
			messageDigest.update(b, off, _t);
		} else {
			if (digest == null) {
				digest = messageDigest.digest();
			}
		}
		return _t;
	}
	@Override public boolean markSupported() {
		return false;
	}

	@Override 
	public String toString() {
		return StringSupport.hexEncode(getDigest());
	}
}
