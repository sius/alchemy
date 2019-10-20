package liquer.alchemy.xmlcrypto.crypto;

import liquer.alchemy.xmlcrypto.support.BaseN;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.function.Function;

public final class CryptoSupport {

	private static final String PROVIDER;
	static {
		Security.setProperty("crypto.policy", "unlimited");
		if (Security.getProvider("BC") != null) {
			PROVIDER = "BC";
		} else {
			PROVIDER = "SUN";
		}
	}

	private CryptoSupport() { }
	/*
	    DigestUtils.sha512(data); // Sensitive
        DigestUtils.sha512(stream); // Sensitive
        DigestUtils.sha512(str); // Sensitive
        DigestUtils.sha512Hex(data); // Sensitive
        DigestUtils.sha512Hex(stream); // Sensitive
        DigestUtils.sha512Hex(str); // Sensitive
	 */
	/**
	 *
	 * @param value
	 * @param algorithm
	 * @param key
	 * @param passphrase
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] sign(String value, String algorithm, byte[] key, char[] passphrase, Charset charset) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		final Charset finalCharset = charset == null ? StandardCharsets.UTF_8: charset;

		if (algorithm.startsWith("Hmac")) {
			Mac mac = getMac(algorithm, key);
			return mac.doFinal(value.getBytes(finalCharset));
		} else {
			Signature sig = getPrivateSignature(algorithm, key, passphrase);
			sig.update(value.getBytes(finalCharset));
			return sig.sign();
		}
	}

	/**
	 *
	 * @param value
	 * @param algorithm
	 * @param key
	 * @param passphrase
	 * @param charset
	 * @param encoder
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String sign(String value, String algorithm, byte[] key, char[] passphrase, Charset charset, Function<byte[], String> encoder) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		final Function<byte[], String> enc = encoder == null ? BaseN::base64UrlEncode : encoder;
		return enc.apply(sign(value, algorithm, key, passphrase, charset));
	}

	/*
	 * @param data byte array
	 * @return the null/zero terminated array
	 */
	private static byte[] zeroTerminate(byte[]  data) {
		if (data == null)  return new byte[] { 0 };
		byte[] copy = new byte[data.length+1];
		System.arraycopy(data, 0, copy, 0, data.length);
		copy[data.length] = 0;
		return copy;
	}

	/**
	 *
	 * @param value
	 * @param algorithm
	 * @param nullTerminated
	 * @param charset
	 * @return
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] hash(String value, String algorithm, boolean nullTerminated, Charset charset) throws NoSuchProviderException, NoSuchAlgorithmException {
		if (value  == null)  return null;
		byte[] data = value.getBytes(charset == null ? StandardCharsets.UTF_8: charset);
		if (nullTerminated) {
			data = zeroTerminate(data);
		}
        MessageDigest md;
        md = MessageDigest.getInstance(algorithm, PROVIDER);
        return  md.digest(data);

	}

	/**
	 *
	 * @param value
	 * @param algorithm
	 * @param nullTerminated
	 * @param charset
	 * @param encoder
	 * @return
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public static String hash(String value, String algorithm, boolean nullTerminated, Charset charset, Function<byte[], String> encoder) throws NoSuchProviderException, NoSuchAlgorithmException {
		final Function<byte[], String> enc = encoder == null ? BaseN::base64UfsEncode : encoder;
        return enc.apply(hash(value, algorithm, nullTerminated, charset));
    }

	/**
	 *
	 * @param algorithm
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static Mac getMac(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac;

		mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));

		return mac;
	}

	/**
	 * Create a signature instance for verification
	 * @param algorithm the signature algorithm
	 * @param publicKey the public key
	 * @return the Signature instance initialized for signature verification
	 */
	public static Signature getPublicSignature(String algorithm, byte[] publicKey) {
		PublicKey pk;
		try {
			Signature publicSignature = Signature.getInstance(algorithm);
			pk = readPublicKeyFromX509Cert(publicKey);
			publicSignature.initVerify(pk);
			return publicSignature;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a signature instance for signing
	 * @param algorithm the signature algorithm
	 * @param privateKey the private key
	 * @return the Signature instance initialized for signing
	 */
	public static Signature getPrivateSignature(String algorithm, byte[] privateKey, char[] passphrase) {
		PrivateKey pk;
		try {
			Signature privateSignature = Signature.getInstance(algorithm);
			pk = new PKCS8Data(privateKey, passphrase).getPrivateKey();
			privateSignature.initSign(pk);
			return privateSignature;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Read X.509 Certificate.
	 *
	 * @param cert
	 *            the cert as a byte array.
	 * @return extracted public key.
	 */
	public static PublicKey readPublicKeyFromX509Cert(final byte[] cert)  {
		try {
			return readX509Certificate(cert).getPublicKey();
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
	}

	public static X509Certificate readX509Certificate(final byte[] cert) throws CertificateException {
		byte[] data = cert.clone();
		if (((char) data[0]) != '-') {
			StringBuilder b = new StringBuilder();
			b.append(Identifier.BEGIN_CERT)
					.append('\n')
					.append(new String(data, StandardCharsets.UTF_8))
					.append('\n')
					.append(Identifier.END_CERT);
			data = b.toString().getBytes(StandardCharsets.UTF_8);
		}

		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		return (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(data));
	}

	/**
	 *
	 * @param value
	 * @param signature
	 * @param algorithm
	 * @param key
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verify(String value, byte[] signature, String algorithm, byte[] key, Charset charset) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		if (signature == null || key == null) {
			return false;
		}

		final Charset finalCharset = charset == null ? StandardCharsets.UTF_8 : charset;

		if (algorithm.startsWith("Hmac")) {
			Mac mac = getMac(algorithm, key);
			int hashByteLen = mac.getMacLength() / 8;
			byte[] computed = mac.doFinal(value.getBytes(finalCharset));
			if (hashByteLen != computed.length || hashByteLen != signature.length) return false;
			for (int i = 0; i < computed.length; i++) {
				if ((computed[i] != signature[i])) return false;
			}
			return true;
		} else {
			Signature sig = getPublicSignature(algorithm, key);
			sig.update(value.getBytes(finalCharset));
			return sig.verify(signature);
		}
	}
}
