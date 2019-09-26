package liquer.alchemy.crypto.ec;

public enum ECNamedCurve {
	secp112r1,
	secp112r2,
	secp128r1,
	secp128r2,
	secp160k1,
	secp160r1,
	secp160r2,
	secp192k1,
	secp192r1,
	secp224k1,
	secp224r1,
	secp256k1,
	secp256r1,
	secp384r1,
	secp521r1,
	X9_62_prime192v2("X9.62 prime192v2"),
	X9_62_prime192v3("X9.62 prime192v3"),
	X9_62_prime239v1("X9.62 prime239v1"),
	X9_62_prime239v2("X9.62 prime239v2"),
	X9_62_prime239v3("X9.62 prime239v3"),
	sect113r1,
	sect113r2,
	sect131r1,
	sect131r2,
	sect163k1,
	sect163r1,
	sect163r2,
	sect193r1,
	sect193r2,
	sect233k1,
	sect233r1,
	sect239k1,
	sect283k1,
	sect283r1,
	sect409k1,
	sect409r1,
	sect571k1,
	sect571r1,
	X9_62_c2tnb191v1("X9.62 c2tnb191v1"),
	X9_62_c2tnb191v2("X9.62 c2tnb191v2"),
	X9_62_c2tnb191v3("X9.62 c2tnb191v3"),
	X9_62_c2tnb239v1("X9.62 c2tnb239v1"),
	X9_62_c2tnb239v2("X9.62 c2tnb239v2"),
	X9_62_c2tnb239v3("X9.62 c2tnb239v3"),
	X9_62_c2tnb359v1("X9.62 c2tnb359v1"),
	X9_62_c2tnb431r1("X9.62 c2tnb431r1"),
	brainpoolP160r1,
	brainpoolP192r1,
	brainpoolP224r1,
	brainpoolP256r1,
	brainpoolP320r1,
	brainpoolP384r1,
	brainpoolP512r1

	;

	ECNamedCurve() {

	}

	ECNamedCurve(String id) {

	}
}
