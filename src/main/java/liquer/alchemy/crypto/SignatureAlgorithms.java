package liquer.alchemy.crypto;


import liquer.alchemy.crypto.alg.SignatureAlgorithm;

public enum SignatureAlgorithms implements SignatureAlgorithm {
	/**
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    |    Algorithm     | Block  | Output | Trunc. |  Key   | Algorithm  |
	 *    |       ID         |  Size  | Length | Length | Length |   Type     |
	 *    +==================+========+========+========+========+============+
	 *    | HMAC-SHA-256-128 |   512  |   256  |  128   |  256   | auth/integ |
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    | HMAC-SHA-384-192 |  1024  |   384  |  192   |  384   | auth/integ |
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    | HMAC-SHA-512-256 |  1024  |   512  |  256   |  512   | auth/integ |
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    | PRF-HMAC-SHA-256 |   512  |   256  | (none) |  var   |     PRF    |
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    | PRF-HMAC-SHA-384 |  1024  |   384  | (none) |  var   |     PRF    |
	 *    +------------------+--------+--------+--------+--------+------------+
	 *    | PRF-HMAC-SHA-512 |  1024  |   512  | (none) |  var   |     PRF    |
	 *    +------------------+--------+--------+--------+--------+------------+
	 */
	HSHA1(
	    Identifier.HMAC_SHA1,
        "HmacSHA1",
        0,
        0,
        0,
        0,
        false,
        null),

	HS256(
	    Identifier.HMAC_SHA256,
        "HmacSHA256",
        512,
        256,
        128,
        256,
        false,
        null),


	HS384(
	    Identifier.HMAC_SHA384,
        "HmacSHA384",
        1024,
        384,
        192,
        512,
        false,
        null),

	HS512(
	    Identifier.HMAC_SHA512,
        "HmacSHA512",
        1024,
        512,
        256,
        512,
        false,
        null),

    RSHA1(
        Identifier.RSA_WITH_SHA1,
        "SHA1withRSA",
        0,
        0,
        0,
        0,
        true,
        "RSA"),

	RS256(
	    Identifier.RSA_WITH_SHA256,
        "SHA256withRSA",
        512,
        256,
        128,
        512,
        true,
        "RSA"),

    RS384(
	    Identifier.RSA_WITH_SHA384,
        "SHA384withRSA",
        1024,
        384,
        192,
        512,
        true,
        "RSA"),

    RS512(
	    Identifier.RSA_WITH_SHA512,
        "SHA512withRSA",
        1024,
        512,
        256,
        512,
        true,
        "RSA"),

    ES256(
	    Identifier.ECDSA_WITH_SHA256,
        "SHA256withECDSA",
        512,
        256,
        128,
        512, true,
        "EC"),

    ES384(
	    Identifier.ECDSA_WITH_SHA384,
        "SHA384withECDSA",
        512,
        256,
        128,
        512,
        true,
        "EC"),

    ES512(
	    Identifier.ECDSA_WITH_SHA512,
        "SHA512withECDSA",
        512,
        256,
        128,
        512,
        true,
        "EC"),

    // PS256(""),
	// PS384(""),
	// PS512(""),
	;

    private final java.lang.String identifier;
	private final java.lang.String algorithm;
	private final int blockSize;
	private final int outputLength;
	private final int truncatedLength;
	private final int keyLength;
	private final boolean keyPair;
	private final java.lang.String keyPairGeneratorAlgorithm;

	SignatureAlgorithms(
			java.lang.String identifier,
			java.lang.String algorithm,
			int blockSize,
			int outputLength,
			int truncatedLength,
			int keyLength,
			boolean keyPair,
			java.lang.String keyPairGeneratorAlgorithm) {
		    this.identifier = identifier;
	        this.algorithm = algorithm;
		    this.blockSize = blockSize;
		    this.outputLength = outputLength;
		    this.truncatedLength = truncatedLength;
		    this.keyLength = keyLength;
		    this.keyPair = keyPair;
		    this.keyPairGeneratorAlgorithm = keyPairGeneratorAlgorithm;
	}

	@Override
	public java.lang.String getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public int getBlockSize() {
		return blockSize;
	}

	@Override
	public int getOutputLength() {
		return outputLength;
	}

	@Override
	public int getTruncatedLength() {
		return truncatedLength;
	}

	@Override
	public int getKeyLength() {
		return keyLength;
	}

	@Override
    public boolean isKeyPair() { return keyPair; }

    @Override
    public java.lang.String getKeyPairGeneratorAlgorithm() { return keyPairGeneratorAlgorithm; }

    @Override
    public java.lang.String getIdentifier() {
        return identifier;
    }
}
