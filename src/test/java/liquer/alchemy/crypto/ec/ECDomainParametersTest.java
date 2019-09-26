package liquer.alchemy.crypto.ec;

import liquer.alchemy.athanor.json.Json;
import org.junit.Test;

import java.math.BigInteger;

public class ECDomainParametersTest {

	@Test
	public void log2() {
		int expected = 8;


		BigInteger big = new BigInteger("fd", 16); //FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37"));
		System.out.println(big.toString());
//		int actual = ECDomainParameters.log2(big);
//		assertEquals(expected, actual);

//		System.out.println(big.shiftRight(2));
//		System.out.println(big.shiftLeft(1));

		Json.println(ECDomainParameters.log2(big));

	}

}
