package liquer.alchemy.crypto;

import liquer.alchemy.alembic.BaseN;
import liquer.alchemy.alembic.IOSupport;
import liquer.alchemy.alembic.StringSupport;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tell the Riemann Hypothesis crew
 * That zeta alone will not do.
 * Precise applications
 * to nice situations
 * Require it for L-functions too.
 * 				- Martin Huxley -
 * http://empslocal.ex.ac.uk/people/staff/mrwatkin//isoc/huxleyNYClimericks.htm
 * found at http://stackoverflow.com/questions/4319496/how-to-encrypt-and-decrypt-data-in-java
 */
public final class CryptoSupport {


	private CryptoSupport() { }


	public static byte[] sign(String value, Signature algorithm, byte[] key) {
		return sign(value, algorithm.getName(), key, StandardCharsets.UTF_8);
	}
	public static byte[] sign(String value, Signature algorithm, byte[] key, Charset charset) {
		return sign(value, algorithm.getName(), key, charset);
	}
	public static byte[] sign(String value, String algorithm, byte[] key) {
		return sign(value, algorithm, key, StandardCharsets.UTF_8);
	}
	public static byte[] sign(String value, String algorithm, byte[] key, Charset charset) {
		final Charset finalCharset = charset == null ? StandardCharsets.UTF_8: charset;

		if (algorithm.startsWith("Hmac")) {
			Mac mac = getMac(algorithm, key);
			return mac.doFinal(value.getBytes(finalCharset));
		} else {
			java.security.Signature sig = getPrivateSignature(algorithm, key);
			try {
				sig.update(value.getBytes(finalCharset));
				return sig.sign();
			} catch (SignatureException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public static String sign(String value, String algorithm, byte[] key, Charset charset, Function<byte[], String> encoder) {
		final Function<byte[], String> enc = encoder == null ? BaseN::base64UrlEncode : encoder;
		return enc.apply(sign(value, algorithm, key, charset));
	}
	public static byte[] sign(InputStream value, String algorithm, byte[] key) throws IOException {
		Mac mac = getMac(algorithm, key);
		return mac.doFinal(IOSupport.toByteArray(value));
	}
	public static String sign(InputStream value, String algorithm, byte[] key, Function<byte[], String> encoder) throws IOException {
		final Function<byte[], String> enc = encoder == null ? BaseN::base64UfsEncode : encoder;
		return enc.apply(sign(value, algorithm, key));
	}

	/**
	 * @param data byte array
	 * @return the null/zero terminated array
	 */
	public static byte[] zeroTerminate(byte[]  data) {
		if (data == null)  return new byte[] { 0 };
		byte[] copy = new byte[data.length+1];
		System.arraycopy(data, 0, copy, 0, data.length);
		copy[data.length] = 0;
		return copy;
	}
	public static byte[] hash(String value, String algorithm, boolean nullTerminated) {
		return hash(value, algorithm, nullTerminated, StandardCharsets.UTF_8);
	}
	public static byte[] hash(String value, String algorithm, boolean nullTerminated, Charset charset) {
		if (value  == null)  return null;
		byte[] data = value.getBytes(charset == null ? StandardCharsets.UTF_8: charset);
		if (nullTerminated) data = zeroTerminate(data);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return  md.digest(data);

	}
	public static String hash(String value, String algorithm, boolean nullTerminated, Charset charset, Function<byte[], String> encoder) {
		final Function<byte[], String> enc = encoder == null ? BaseN::base64UfsEncode : encoder;
        return enc.apply(hash(value, algorithm, nullTerminated, charset));
    }
	/**
	 * @param value value
	 * @return the base64UrlEncoded digest String
	 */
	public static String md5(String value) {
		return md5(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digest String
	 */
	public static String md5(String value, boolean zeroTerminated) {
		return md5(value, zeroTerminated, BaseN::base64UrlEncode);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String md5(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
		return hash(value, "MD5", zeroTerminated, StandardCharsets.UTF_8, encoder);
	}
	/**
	 * @param value value
	 * @return tbe base64UrlEncoded digest String
	 */
	public static String sha1(String value) {
		return sha1(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha1(String value, boolean zeroTerminated) {
	    return sha1(value, zeroTerminated, BaseN::base64UrlEncode);
    }
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String sha1(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
	   return  hash(value, "SHA-1", zeroTerminated, StandardCharsets.UTF_8, encoder);
	}
	/**
	 * @param value value
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha256(String value) {
		return sha256(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha256(String value, boolean zeroTerminated) {
		return sha256(value, zeroTerminated, BaseN::base64UrlEncode);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String sha256(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
        return hash(value, "SHA-256", zeroTerminated, StandardCharsets.UTF_8, encoder);
    }
	/**
	 * @param value value
	 * @return the base64UrlEncoded digestString
	 */
	public static String sha384(String value) {
		return sha384(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha384(String value, boolean zeroTerminated) {
		return StringSupport.hexEncode(hash(value, "SHA-384", zeroTerminated));
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String sha384(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
	    return hash(value, "SHA-384", zeroTerminated, StandardCharsets.UTF_8, encoder);
    }

	/**
	 * @param value value
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha224(String value) {
		return sha224(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digestString
	 */
	public static String sha224(String value, boolean zeroTerminated) {
		return sha224(value, zeroTerminated, BaseN::base64UrlEncode);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String sha224(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
	    return hash(value, "SHA-224", zeroTerminated, StandardCharsets.UTF_8, encoder);
    }
	/**
	 * @param value value
	 * @return the base64UrlEncoded digest String
	 */
	public static String sha512(String value) {
		return sha512(value, false);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @return the base64UrlEncoded digestString
	 */
	public static String sha512(String value, boolean zeroTerminated) {
		return sha512(value, zeroTerminated, BaseN::base64UrlEncode);
	}
	/**
	 * @param value value
	 * @param zeroTerminated zeroTerminated
	 * @param encoder encoder
	 * @return the encoded digest String
	 */
	public static String sha512(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
	    return hash(value, "SHA-512", zeroTerminated, StandardCharsets.UTF_8, encoder);
    }

	public static String ripemd160(String value) {
		return ripemd160(value, false);
	}
	public static String ripemd160(String value, boolean zeroTerminated) {
		return ripemd160(value, zeroTerminated, StringSupport::hexEncode);
	}
	public static String ripemd160(String value, boolean zeroTerminated, Function<byte[], String> encoder) {
		return hash(value, "RIPEMD-160", zeroTerminated, StandardCharsets.UTF_8, encoder);
	}
	public static String hmacMd5(String value, byte[] key) {
		return hmacMd5(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacMd5(String value, byte[] key, Charset charset) {
		return hmacMd5(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacMd5(String value, byte[] key, Charset charset, Function<byte[], String> encoder) {
		return sign(value, "HmacMD5", key, charset, encoder);
	}
	public static String hmacSha1(String value, byte[] key) {
		return hmacSha1(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacSha1(String value, byte[] key, Charset charset) {
		return hmacSha1(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacSha1(String value, byte[] key, Charset charset,  Function<byte[], String> encoder) {
		return sign(value, "HmacSHA1", key, charset, encoder);
	}
	public static String hmacSha256(String value, byte[] key) {
		return hmacSha256(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacSha256(String value, byte[] key, Charset charset) {
		return hmacSha256(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacSha256(String value, byte[] key, Charset charset, Function<byte[], String> encoder) {
		return sign(value, "HmacSHA256", key, charset, encoder);
	}
	public static String hmacSha384(String value, byte[] key) {
		return hmacSha384(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacSha384(String value, byte[] key, Charset charset) {
		return hmacSha384(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacSha384(String value, byte[] key, Charset charset,  Function<byte[], String> encoder){
		return sign(value, "HmacSHA384", key, charset, encoder);
	}
	public static String hmacSha512(String value, byte[] key) {
		return hmacSha512(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacSha512(String value, byte[] key, Charset charset) {
		return hmacSha512(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacSha512(String value, byte[] key, Charset charset, Function<byte[], String> encoder) {
		return sign(value, "HmacSHA512", key, charset, encoder);
	}
	public static String hmacRipemd160(String value, byte[] key) {
		return hmacRipemd160(value, key, StandardCharsets.UTF_8);
	}
	public static String hmacRipemd160(String value, byte[] key, Charset charset) {
		return hmacRipemd160(value, key, charset, StringSupport::hexEncode);
	}
	public static String hmacRipemd160(String value, byte[] key, Charset charset, Function<byte[], String> encoder) {
		return sign(value, "HmacRIPEMD160", key, charset, encoder);
	}
	public static byte[] getPBKDF2WithHmacSHA1(String value, byte[] key, int iterations, int hashBytes) {
		iterations = Math.max(1, iterations);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey tmp = factory.generateSecret(new PBEKeySpec(value.toCharArray(), key, iterations, hashBytes * 8));
            return new SecretKeySpec(tmp.getEncoded(), "AES").getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

	}
	public static boolean verify(String value, byte[] signature, String algorithm, byte[] key) {
		return verify(value, signature, algorithm, key, null);
	}

	public static Mac getMac(String algorithm, byte[] key) {
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
		return mac;
	}

	/**
	 * Create a signature instance for verification
	 * @param algorithm the signature algorithm
	 * @param publicKey the public key
	 * @return the Signature instance initialized for signature verification
	 */
	public static java.security.Signature getPublicSignature(String algorithm, byte[] publicKey) {
		PublicKey pk;
		try {
			java.security.Signature publicSignature = java.security.Signature.getInstance(algorithm);
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
	public static java.security.Signature getPrivateSignature(String algorithm, byte[] privateKey) {
		PrivateKey pk;
		try {
			java.security.Signature privateSignature = java.security.Signature.getInstance(algorithm);
			pk = readPrivateKey(privateKey);
			privateSignature.initSign(pk);
			return privateSignature;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read a PKCS#8 encoded private key. May be DER or PEM encoded.
	 *
	 * @param key
	 *            the private key data as a byte array.
	 * @return parsed private key.
	 */
	public static PrivateKey readPrivateKey(final byte[] key) {
		byte[] data = key;
		// decode from PEM format
		if (((char) data[0]) == '-') {
			data = convertPEMToDER(new String(data, StandardCharsets.US_ASCII));
		}

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);

		try {
			return KeyFactory.getInstance("RSA").generatePrivate(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			try {
				return KeyFactory.getInstance("DSA").generatePrivate(spec);
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e2) {
				throw new RuntimeException(e2);
			}
		}
	}

	/**
	 * Read X509 encoded public key. May be DER or PEM encoded.
	 *
	 * @param key
	 *            the public key as a byte array.
	 * @return parsed public key.
	 */
	public static PublicKey readPublicKey(final byte[] key) {
		byte[] data = key;
		// decode from PEM format
		if (((char) data[0]) == '-') {
			data = convertPEMToDER(new String(data, StandardCharsets.UTF_8));
		}

		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);

		try {
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			try {
				return KeyFactory.getInstance("DSA").generatePublic(spec);
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e2) {
				throw new RuntimeException(e2);
			}
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
		byte[] data = cert;
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
	 * @param pem
	 * @return
	 */
	public static byte[] convertPEMToDER(final String pem) {
		List<String> lines = new LinkedList<>(Arrays.asList(pem.split("\n")));
		String header = lines.remove(0).trim();
		String footer = lines.remove(lines.size() - 1).trim();
		String type;

		if (header.startsWith("-----BEGIN ") && header.endsWith("-----")) {
			type = header;
			type = type.replace("-----BEGIN ", "");
			type = type.replace("-----", "");

			if (type.contains("ENCRYPTED")) {
				throw new IllegalArgumentException("Encrypted keys are not supported.");
			}

			if (footer.equals("-----END " + type + "-----")) {
				// expected match
				String encoded = lines.stream().collect(Collectors.joining(""));
				return Base64.getDecoder().decode(encoded);
				// return BaseN.base64Decode(encoded);
			} else {
				throw new IllegalArgumentException("Unexpected PEM footer '" + footer + "'");
			}
		} else {
			throw new IllegalArgumentException("Unexpected PEM header '" + header + "'");
		}
	}

	/**
	 *
	 * @param value
	 * @param signature
	 * @param algorithm
	 * @param key
	 * @param charset
	 * @return
	 */
	public static boolean verify(String value, byte[] signature, String algorithm, byte[] key, Charset charset) {
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
			java.security.Signature sig = getPublicSignature(algorithm, key);
			try {
				sig.update(value.getBytes(finalCharset));
				return sig.verify(signature);
			} catch (SignatureException e) {
				throw new RuntimeException(e);
			}
		}
	}

    /**
     * create a byte array from a char array with defaultCharset
     * @param text text
     * @return the byte array data
     */
    public byte[] toByteArray(char[] text) {
        return Charset.defaultCharset().encode(CharBuffer.wrap(text)).array();
    }
    /**
     * create a byte array from a char array with specified charSet, e.g. UTF-8
     * @param text text
     * @param encoding encoding
     * @return the byte array data
     */
    public byte[] toByteArray(char[] text, String encoding) {
        return Charset.forName(encoding).encode(CharBuffer.wrap(text)).array();
    }
    /**
     * create a byte array from a char array with specified charSet, e.g. UTF-8
     * @param text text
     * @param charset charset
     * @return the byte array data
     */
    public byte[] toByteArray(char[] text, Charset charset) {
        return charset.encode(CharBuffer.wrap(text)).array();
    }
    /**
     * Symmetric encryption uses the same key for both encryption and decryption.
     * The key for the cipher should be an instance of javax.crypto.spec.SecretKeySpec.
     * AES in particular requires its key to be created with exactly 128 bits (16 bytes).
     * @param clearText clearText
     * @param key key
     * @return the encrypted data
     * @throws NoSuchAlgorithmException Exception
     * @throws NoSuchPaddingException Exception
     * @throws InvalidKeyException Exception
     * @throws IllegalBlockSizeException Exception
     * @throws BadPaddingException Exception
     */
    public static byte[] encryptSymmetric(byte[] clearText, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aes.init(Cipher.ENCRYPT_MODE, key);
        return aes.doFinal(clearText);
    }
    /**
     * Symmetric encryption uses the same key for both encryption and decryption.
     * @param cipherText cipherText
     * @param key key
     * @return the decrypted string
     * @throws NoSuchAlgorithmException Exception
     * @throws NoSuchPaddingException Exception
     * @throws InvalidKeyException Exception
     * @throws IllegalBlockSizeException Exception
     * @throws BadPaddingException Exception
     */
    public static String decryptSymmetric(byte[] cipherText, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aes.init(Cipher.DECRYPT_MODE, key);
        return new String(aes.doFinal(cipherText));
    }
    /**
     * Asymmetric encryption, also called public key encryption, uses a key pair.
     * One part of the key is used to encrypt and the other to decrypt.
     * This allows you to make the encryption key public, allowing anyone to generate messages only you,
     * the holder of the private decryption key, can read.
     * Alternatively, you can encrypt with the private key, useful for digital signatures.
     * The keys will be instances of java.security.PublicKey and java.security.PrivateKey
     * @param clearText clearText
     * @param publicKey publicKey
     * @return the encrypted data
     * @throws NoSuchAlgorithmException Exception
     * @throws NoSuchPaddingException Exception
     * @throws InvalidKeyException Exception
     * @throws IllegalBlockSizeException Exception
     * @throws BadPaddingException Exception
     */
    public static byte[] encryptAsymmetric(byte[] clearText, Key publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsa.doFinal("my cleartext".getBytes());
    }
    /**
     * Asymmetric encryption, also called public key encryption, uses a key pair.
     * One part of the key is used to encrypt and the other to decrypt.
     * This allows you to make the encryption key public, allowing anyone to generate messages only you,
     * the holder of the private decryption key, can read.
     * Alternatively, you can encrypt with the private key, useful for digital signatures.
     * The keys will be instances of java.security.PublicKey and java.security.PrivateKey
     * @param cipherText cipherText
     * @param privateKey privateKey
     * @return the decrypted String
     * @throws NoSuchAlgorithmException Exception
     * @throws NoSuchPaddingException Exception
     * @throws InvalidKeyException Exception
     * @throws IllegalBlockSizeException Exception
     * @throws BadPaddingException Exception
     */
    public static String decryptAsymmetric(byte[] cipherText, Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(rsa.doFinal(cipherText));
    }

    /**
     * Generate a new KeyPair:
     * @see KeyPair#getPublic();
     * @see KeyPair#getPrivate();
     * @return the encrypted data
     * @throws NoSuchAlgorithmException Exception
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        return keyPairGenerator.generateKeyPair();
    }
    /**
     * transformed into byte arrays for storage and transmission
     * @param key key
     * @return the encoded data
     */
    public static byte[] encodedKey(Key key) {
        return key.getEncoded();
    }
    /**
     * transform to X.509Encoded Cert
     * @param encodedKey encodedKey
     * @return the KeySpec
     */
    public static KeySpec createX509EncodedKeySpec(byte[] encodedKey) {
        return new X509EncodedKeySpec(encodedKey);

    }
    /**
     * extract public Key from Cert
     * @param keySpec keySpec
     * @return the PublicKey
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeySpecException Exception
     */
    public static PublicKey publicKey(KeySpec keySpec) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    /**
     * extract private Key from Cert
     * @param keySpec keySpec
     * @return the Private Key
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeySpecException Exception
     */
    public static PrivateKey privateKey(KeySpec keySpec) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    /**
     * SHA1 digest
     * @param passPhrase passPhrase
     * @return the digest
     * @throws NoSuchAlgorithmException Exception
     */
    public static byte[] digest(String passPhrase) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(passPhrase.getBytes());
        return digest.digest();
    }
    /**
     * @param passPhrase passPhrase
     * @return the crypto digest
     * @throws NoSuchAlgorithmException Exception
     */
    public static byte[] cryptoDigest(String passPhrase) throws NoSuchAlgorithmException {
        return new SecretKeySpec(digest(passPhrase), 0, 16, "AES").getEncoded();
    }
    /**
     * Password-Based Key Derivation Function 2 (PBKDF2) is an algorithm specially designed
     * for generating keys from passwords that is considered more secure than a simple SHA1 hash.
     * The salt ensures your encryption won't match another encryption using the same key and clear text
     * and helps prevent dictionary attacks.
     * The iterations value is an adjustable parameter.
     * Higher values use more computing power, making brute force attacks more difficult.
     * @param passPhrase passPhrase
     * @param salt salt
     * @param iterations iterations
     * @return the crypto digest
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeySpecException Exception
     */
    public static byte[] cryptoDigestPBKDF2(String passPhrase,  byte[] salt, int iterations, int hashBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        iterations = Math.max(1, iterations);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey tmp = factory.generateSecret(new PBEKeySpec(passPhrase.toCharArray(), salt, iterations, hashBytes * 8));
        return new SecretKeySpec(tmp.getEncoded(), "AES").getEncoded();
    }
}
