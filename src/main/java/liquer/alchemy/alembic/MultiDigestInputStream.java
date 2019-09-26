package liquer.alchemy.alembic;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import static liquer.alchemy.support.StringSupport.hexEncode;

@SuppressWarnings("unused")
public class MultiDigestInputStream extends FilterInputStream {

	private MessageDigest[] digests;
	private Map<String, byte[]> digestMap;
	/**
	 * Returns a Filter InputStream that build a MD5 and SHA1 Message digest while reading the InputStream
	 * @param in an InputStream
	 * @throws NoSuchAlgorithmException MessageDigest SignatureAlgorithm not found
	 */
	public MultiDigestInputStream(InputStream in) throws NoSuchAlgorithmException {
		this(in, MessageDigest.getInstance("MD5"), MessageDigest.getInstance("SHA1"));
	}
	/**
	 * Returns a Filter InputStream that build the specified Message digests while reading the InputStream
	 * @param in an InputStream
	 * @param digests a MessageDigest
	 */
	public MultiDigestInputStream(InputStream in, MessageDigest... digests) {
		super(in);
		this.digests = digests;
	}
	
	public byte[] getDigest(String algorithm) {
		if (digestMap != null && digestMap.containsKey(algorithm)) {
			return digestMap.get(algorithm);
		}
		return new byte[0];
	}
	@Override public int read() throws IOException {
		int _t = super.read();
		if (_t > -1) {
			for(MessageDigest md : digests ) {
				md.update((byte)_t);
			}
		} else {
			if (digestMap == null) {
				digestMap = new LinkedHashMap<>();
				for(MessageDigest md : digests) {
					digestMap.put(md.getAlgorithm(), md.digest());
				}
			}
		}
		return _t;
	}
	@Override 
	public int read(byte[] b) throws IOException {
		int _t = super.read(b);
		if (_t > -1) {
			for(MessageDigest md : digests ) {
				md.update(b, 0, _t);
			}
		} else {
			if (digestMap == null) {
				digestMap = new LinkedHashMap<>();
				for(MessageDigest md : digests) {
					digestMap.put(md.getAlgorithm(), md.digest());
				}
			}
		}
		return _t;
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int _t = super.read(b, off, len);
		if (_t > -1) {
			for(MessageDigest md : digests) {
				md.update(b, off, _t);
			}
		} else {
			if (digestMap == null) {
				digestMap = new LinkedHashMap<>();
				for(MessageDigest md : digests) {
					digestMap.put(md.getAlgorithm(), md.digest());
				}
			}
		}
		return _t;
	}
	
	@Override public boolean markSupported() {
		return false;
	}

	/**
	 * @return the hexEncoded SHA1 digest or empty
	 */
	public String toSHA1() {
		return hexEncode(getDigest("SHA1"));
	}
	/**
	 * @return the hexEncoded MD5 digest or empty
	 */
	public String toMD5() {
		return hexEncode(getDigest("MD5"));
	}

	@Override 
	public String toString() {
		if (digests == null || digests.length == 0) {
			return "";
		}
		return hexEncode(getDigest(digests[0].getAlgorithm()));
	}
}
