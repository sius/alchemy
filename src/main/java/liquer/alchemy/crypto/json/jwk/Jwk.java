package liquer.alchemy.crypto.json.jwk;

import liquer.alchemy.alembic.TypedMap;
import liquer.alchemy.crypto.Signature;
import liquer.alchemy.crypto.ec.ECNamedCurve;
import liquer.alchemy.crypto.ec.NamedCurveParameterSpec;
import liquer.alchemy.athanor.json.Json;
import liquer.alchemy.support.BaseN;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.*;
import java.util.stream.Collectors;

import static liquer.alchemy.crypto.json.jwk.JwkParameter.*;

public final class Jwk extends TypedMap {

	public Jwk() {
		putAll(new HashMap<>());
	}

	public Jwk(Signature signatureAlgorithm, Key key)  {
		this();
		if (key instanceof SecretKey) {
			setKeyType(KeyType.oct);
			setKey((SecretKey)key);
		} else {
			if (key instanceof RSAKey) {
				setKeyType(KeyType.RSA);
				if (key instanceof RSAPublicKey) {
					RSAPublicKey pk = (RSAPublicKey)key;
					setModulus(pk.getModulus());
					setPublicExponent(pk.getPublicExponent());
				} else if (key instanceof RSAPrivateCrtKey) {
					RSAPrivateCrtKey pk = (RSAPrivateCrtKey)key;
					setModulus(pk.getModulus());
					setPublicExponent(pk.getPublicExponent());
					setPrivateExponent(pk.getPrivateExponent());
					setPrimeP(pk.getPrimeP());
					setPrimeQ(pk.getPrimeQ());
					setPrimeExponentP(pk.getPrimeExponentP());
					setPrimeExponentQ(pk.getPrimeExponentQ());
					setCrtCoefficient(pk.getCrtCoefficient());
				}
			} else if (key instanceof ECKey) {
				setKeyType(KeyType.EC);
				if (key instanceof ECPublicKey) {
					ECPublicKey pk = (ECPublicKey)key;
					setECPoint(pk.getW());
					setECParameterSpec(pk.getParams());
				} else if (key instanceof ECPrivateKey) {
					ECPrivateKey pk = (ECPrivateKey)key;
					setECParameterSpec(pk.getParams());

					// Generate public key from private key
//					KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
//					ECParameterSpec ecSpec = NamedCurveParameterSpec.getInstance(ECNamedCurve.secp256r1);
//
//					ECPoint Q = ecSpec.getGenerator().multiply(pk.getS());
//					byte[] publicDerBytes = Q.getEncoded(false);
//
//					ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
//					ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
//					ECPublicKey publicKeyGenerated = (ECPublicKey) keyFactory.generatePublic(pubSpec);

					setECPrivateValueD(pk.getS());
				}
			}
		}
		setAlgorithm(signatureAlgorithm);
		setKeyID(UUID.randomUUID());
	}

	public Jwk(Map<String, Object> map) {
		putAll(map);
	}
	public KeyType getKeyType() {
		return enumValue(KeyType.class, kty, null);
	}

	public void setKeyType(KeyType keyType) {
		put(kty, keyType);
	}

	public PublicKeyUse getPublicKeyUse() {
		return enumValue(PublicKeyUse.class, use, PublicKeyUse.none);
	}

	public void setPublicKeyUse(PublicKeyUse publicKeyUse) {
		put(use, publicKeyUse);
	}

	public KeyOperations[] getKeyOperations() {
		return enumArray(KeyOperations.class, key_ops, null);
	}

	public void setKeyOperations(KeyOperations[] keyOperations) {
		put(key_ops, keyOperations);
	}

	public Signature getAlgorithm() {
		return enumValue(Signature.class, alg, null);
	}

	public void setAlgorithm(Signature SignatureAlgorithm) {
		put(alg, SignatureAlgorithm);
	}

	public Object getKeyID() {
		return get(kid);
	}

	public void setKeyID(Object keyID) {
		put(kid, keyID);
	}

	public URI getX509URL() throws URISyntaxException {
		return new URI(stringValue(x5u, null));
	}
	public void setX509URL(URI x509URL) {
		put(x5u, x509URL);
	}

	public X509CertificateChain getX509CertificateChain() {
		return new X509CertificateChain(valueOf(x5c));
	}
	public void setX509CertificateChain(X509CertificateChain x509CertificateChain) {
		put(x5c, x509CertificateChain);
	}

	public String getX509CertificateSHA1Thumbprint() {
		return stringValue(x5t, null);
	}
	public void setX509CertificateSHA1Thumbprint(String x509CertificateSHA1Thumbprint) {
		put(x5t, x509CertificateSHA1Thumbprint);
	}

	public String getX509CertificateSHA256Thumbprint() {
		return stringValue(x5t_S256, null);
	}
	public void setX509CertificateSHA256Thumbprint(String x509CertificateSHA256Thumbprint) {
		put(x5t_S256, x509CertificateSHA256Thumbprint);
	}

	public List<Jwk> getKeys() {
		return listOf(keys, new ArrayList<>()).stream().map(Jwk::new).collect(Collectors.toList());
	}

	public void setKeys(List<Jwk> keyList) {
		put(keys, keyList);
	}

	/**
	 *
	 * @return a public key
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, InvalidParameterSpecException {
		PublicKey ret = null;
		KeySpec keySpec = null;
		switch(getKeyType()) {
			case EC:
				keySpec = new ECPublicKeySpec(getECPoint(), getECParameterSpec() );
				break;
			case RSA:
				keySpec = new RSAPublicKeySpec(getModulus(), getPublicExponent() );
				break;
			default:
				break;
		}
		if (keySpec != null) {
			KeyFactory kf = KeyFactory.getInstance(getAlgorithm().getKeyPairGeneratorAlgorithm());
			ret = kf.generatePublic(keySpec);
		}
		return ret;
	}

	/**
	 *
	 * @return a private key
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey ret = null;
		KeySpec keySpec = null;
		switch(getKeyType()) {
			case EC:
				// keySpec = new ECPrivateKeySpec();
				break;
			case RSA:
				keySpec  = new RSAPrivateCrtKeySpec(
					getModulus(), getPublicExponent(),
					getPrivateExponent(), getPrimeP(), getPrimeQ(),
					getPrimeExponentP(), getPrimeExponentQ(), getCrtCoefficient());
				break;
			default:
				break;
		}
		if (keySpec != null) {
			KeyFactory kf = KeyFactory.getInstance(getAlgorithm().getKeyPairGeneratorAlgorithm());
			ret = kf.generatePrivate(keySpec);
		}
		return ret;
	}

	/**
	 * JSON key: k
	 * @return a symmetric key or null
	 */
	public SecretKey getKey() {
		SecretKey ret = null;
		if (getKeyType().equals(KeyType.oct)) {
			ret = new SecretKeySpec(
				BaseN.base64Decode(stringValue(k, "")),
				getAlgorithm().getName());
		}
		return ret;
	}

	void setKey(SecretKey key) {
		put(k, BaseN.base64UrlEncode(key.getEncoded()));
	}

	public BigInteger getModulus() {
		return new BigInteger(BaseN.base64Decode(stringValue(n, "")));
	}
	void setModulus(BigInteger modulus) {
		put(n, BaseN.base64UrlEncode(modulus.toByteArray()));
	}
	public BigInteger getPublicExponent() {
		return new BigInteger(BaseN.base64Decode(stringValue(e, "")));
	}
	void setPublicExponent(BigInteger publicExponent) {
		put(e, BaseN.base64UrlEncode(publicExponent.toByteArray()));
	}
	public BigInteger getPrivateExponent() {
		return KeyType.RSA.equals(getKeyType())
			? new BigInteger(BaseN.base64Decode(stringValue(d, "")))
			: null;
	}
	void setPrivateExponent(BigInteger privateExponent) {
		if (KeyType.RSA.equals(getKeyType()) ) {
			put(d, BaseN.base64UrlEncode(privateExponent.toByteArray()));
		}
	}
	public BigInteger getECPrivateValueD() {
		return KeyType.EC.equals(getKeyType())
			? new BigInteger(BaseN.base64Decode(stringValue(d, "")))
			: null;
	}
	void setECPrivateValueD(BigInteger privateValue) {
		if (KeyType.EC.equals(getKeyType()) ) {
			put(d, BaseN.base64UrlEncode(privateValue.toByteArray()));
		}
	}
	public BigInteger getPrimeP() {
		return new BigInteger(BaseN.base64Decode(stringValue(p, "")));
	}
	void setPrimeP(BigInteger primeP) {
		put(p, BaseN.base64UrlEncode(primeP.toByteArray()));
	}
	public BigInteger getPrimeQ() {
		return new BigInteger(BaseN.base64Decode(stringValue(q, "")));
	}
	void setPrimeQ(BigInteger primeQ) {
		put(q, BaseN.base64UrlEncode(primeQ.toByteArray()));
	}
	public BigInteger getPrimeExponentP() {
		return new BigInteger(BaseN.base64Decode(stringValue(dp, "")));
	}
	void setPrimeExponentP(BigInteger primeExponentP) {
		put(dp, BaseN.base64UrlEncode(primeExponentP.toByteArray()));
	}
	public BigInteger getPrimeExponentQ() {
		return new BigInteger(BaseN.base64Decode(stringValue(dq, "")));
	}
	void setPrimeExponentQ(BigInteger primeExponentQ) {
		put(dq, BaseN.base64UrlEncode(primeExponentQ.toByteArray()));
	}
	public BigInteger getCrtCoefficient() {
		return new BigInteger(BaseN.base64Decode(stringValue(qi, "")));
	}
	void setCrtCoefficient(BigInteger crtCoefficient) {
		put(qi, BaseN.base64UrlEncode(crtCoefficient.toByteArray()));
	}
	public ECPoint getECPoint() {
		return new ECPoint(
			new BigInteger(BaseN.base64Decode(stringValue(x, ""))),
			new BigInteger(BaseN.base64Decode(stringValue(y, ""))));
	}
	void setECPoint(ECPoint point) {
		put(x, BaseN.base64UrlEncode(point.getAffineX().toByteArray()));
		put(y, BaseN.base64UrlEncode(point.getAffineY().toByteArray()));
	}

	public ECParameterSpec getECParameterSpec() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidParameterSpecException {
		return NamedCurveParameterSpec.getInstance(enumValue(ECNamedCurve.class, crv, null));
	}
	void setECParameterSpec(ECParameterSpec param) {
		param.toString();
	}

	@Override
	public String toString() {
		return Json.stringify(this, 2);
	}

}
