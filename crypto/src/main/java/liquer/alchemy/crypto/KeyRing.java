package liquer.alchemy.crypto;


import liquer.alchemy.athanor.reflect.Attr;
import liquer.alchemy.crypto.json.jwk.Jwk;
import liquer.alchemy.athanor.json.Json;

import javax.crypto.KeyGenerator;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class KeyRing {

	private final SecureRandom secureRandom = new SecureRandom();

	@Attr
	private final List<Jwk> ring;

	public KeyRing(Jwk jwk) {
		this(jwk.getKeys());
	}
	public KeyRing(List<Jwk> keyRing) {
		this.ring = (keyRing == null) ? new ArrayList<>() : keyRing;
	}
	public KeyRing(Signature algorithm, int ringSize) throws NoSuchAlgorithmException {
		this(algorithm, algorithm.getKeyLength(), ringSize);
	}
	public KeyRing(Signature algorithm, int keyLength, int ringSize) throws NoSuchAlgorithmException {
		if (algorithm.isKeyPair()) {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm.getKeyPairGeneratorAlgorithm());
			keyGen.initialize(keyLength, secureRandom);
			this.ring = IntStream.range(0, Math.max(ringSize, 0))
				.mapToObj( i -> {
					KeyPair keyPair = keyGen.generateKeyPair();
					return new Jwk(algorithm, keyPair.getPrivate());
				})
				.collect(Collectors.toList());
		} else {
			KeyGenerator keyGen = KeyGenerator.getInstance(algorithm.getName());
			keyGen.init(algorithm.getBlockSize(), secureRandom);
			this.ring = IntStream.range(0, Math.max(ringSize, 0))
				.mapToObj( i -> new Jwk(algorithm, keyGen.generateKey()))
				.collect(Collectors.toList());
		}
	}

	@Override
	public String toString() {
		return Json.stringify(this, 2);
	}
	public int nextInt() { return secureRandom.nextInt(); }
	public byte[] nextKey() {
		return getKey(nextInt());
	}
	public byte[] getKey(int i) {
		return getJwk(i).getKey().getEncoded();
	}
	public Jwk getJwk(int i) {
		int next = (i == Integer.MIN_VALUE) ? Integer.MAX_VALUE : i;
		int absNext = Math.abs(next);
		return ring.get(absNext%ring.size());
	}


}
