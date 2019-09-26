package liquer.alchemy.crypto.pwd;
/*
 * Copyright (c) 2012 Cole Barnes [cryptofreek{at}gmail{dot}com]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the &quot;Software&quot;), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 *
 * This is a clean-room implementation of PBKDF2 using RFC 2898 as a reference.
 *
 * RFC 2898:
 * http://tools.ietf.org/html/rfc2898#section-5.2
 *
 * This code passes all RFC 6070 test vectors:
 * http://tools.ietf.org/html/rfc6070
 *
 * The function "nativeDerive()"; is supplied as an example of the native Java
 * PBKDF2WithHmacSHA1 implementation.  It is used for benchmarking and
 * comparison only.
 *
 * The functions "fromHex()" and "toHex()" came from some message board
 * somewhere.  No license was included.
 *
 */

import liquer.alchemy.alembic.StringSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Formatter;


/**
 * Password Hashing with Password-Based Key Derivation Function 2 (PBKDF2)
 */
public class CPbkdf2 {

	private final static Logger LOG = LogManager.getLogger(CPbkdf2.class);
	public static final String PBKDF_2_WITH_HMAC_SHA_1 = "PBKDF2WithHmacSHA1";
	/* START RFC 2898 IMPLEMENTATION */

	/**
	 * @param P password
	 * @param S salt
	 * @param c iterations
	 * @param dkLen key length
	 */
	public static byte[] derive(char[] P, byte[] S, int c, int dkLen) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int hLen = 20;

			if (dkLen > ((Math.pow(2, 32)) - 1) * hLen) {
				System.out.println("derived key too long");
			} else {
				int l = (int) Math.ceil((double) dkLen / (double) hLen);
				// int r = dkLen - (l-1)*hLen;

				for (int i = 1; i <= l; i++) {
					byte[] T = F(P, S, c, i);
					baos.write(T);
				}
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		byte[] baDerived = new byte[dkLen];
		System.arraycopy(baos.toByteArray(), 0, baDerived, 0, baDerived.length);
		return baDerived;
	}

	private static byte[] F(char[] P, byte[] S, int c, int i) throws Exception {
		byte[] U_LAST = null;
		byte[] U_XOR = null;


		SecretKeySpec key = new SecretKeySpec(StringSupport.getEncodedBytes(P, StandardCharsets.UTF_8), "HmacSHA1");
		Mac mac = Mac.getInstance(key.getAlgorithm());
		mac.init(key);

		for (int j = 0; j < c; j++) {
			if (j == 0) {
				byte[] baS = S;
				byte[] baI = INT(i);
				byte[] baU = new byte[baS.length + baI.length];

				System.arraycopy(baS, 0, baU, 0, baS.length);
				System.arraycopy(baI, 0, baU, baS.length, baI.length);

				U_XOR = mac.doFinal(baU);
				U_LAST = U_XOR;
				mac.reset();
			} else {
				byte[] baU = mac.doFinal(U_LAST);
				mac.reset();

				for (int k = 0; k < U_XOR.length; k++) {
					U_XOR[k] = (byte) (U_XOR[k] ^ baU[k]);
				}

				U_LAST = baU;
			}
		}
		return U_XOR;
	}

	private static byte[] INT(int i) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(i);
		return bb.array();
	}
	/* END RFC 2898 IMPLEMENTATION */

	/* START HELPER FUNCTIONS */
	private static String toHex(byte[] ba) {
		String strHex = null;

		if (ba != null) {
			StringBuilder sb = new StringBuilder(ba.length * 2);
			Formatter formatter = new Formatter(sb);

			for (byte b : ba) {
				formatter.format("%02x", b);
			}
			formatter.close();
			strHex = sb.toString().toLowerCase();
		}
		return strHex;
	}

	/**
	 * the native implementation of PBKDF2
	 * this will replace the self implemented version
	 * @param strPassword password char array
	 * @param strSalt salt byte array
	 * @param nIterations number of iterations
	 * @param nKeyLen key length
	 * @return encrypted byte array
	 */
	public static byte[] nativeDerive(char[] strPassword, byte[] strSalt, int nIterations, int nKeyLen) {
		byte[] baDerived = null;
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_1);
			KeySpec ks = new PBEKeySpec(strPassword, strSalt, nIterations, nKeyLen * 8);
			SecretKey s = f.generateSecret(ks);
			baDerived = s.getEncoded();
		} catch (Exception e) {
			LOG.error(e);
		}
		return baDerived;
	}
	/* END HELPER FUNCTIONS */

	public static void runTestVector(char[] P, byte[] S, int c, int dkLen, String strExpectedDk) {
		System.out.println("Input:");
		System.out.println("  P = \"" + new String(P) + "\"");
		System.out.println("  S = \"" + new String(S, StandardCharsets.UTF_8) + "\"");
		System.out.println("  c = " + c);
		System.out.println("  dkLen = " + dkLen);
		System.out.println();

		long nStartDk = System.nanoTime();
		byte[] DK = derive(P, S, c, dkLen);
		long nStopDk = System.nanoTime();

		long nStartDkNative = System.nanoTime();
		byte[] DK_NATIVE = nativeDerive(P, S, c, dkLen);
		long nStopDkNative = System.nanoTime();

		System.out.println("Output:");
		System.out.println("  DK          = " + toHex(DK));
		System.out.println("  DK_NATIVE   = " + toHex(DK_NATIVE));
		System.out.println("  DK_EXPECTED = " + strExpectedDk.replaceAll(" ", ""));
		System.out.println();
		System.out.println("Duration [my implementation]:      " + (nStopDk - nStartDk) + " ns" );
		System.out.println("Duration [native implementation]:  " + (nStopDkNative - nStartDkNative) + " ns" );
		System.out.println("---------------------------------------------------------------");
		System.out.println();
	}
	public static void RFC6070() {

		char[] P = "password".toCharArray();
		byte[] S = "salt".getBytes(StandardCharsets.UTF_8);
		runTestVector(P, S, 1, 20, "0c 60 c8 0f 96 1f 0e 71 f3 a9 b5 24 af 60 12 06 2f e0 37 a6");
		runTestVector(P, S, 2, 20, "ea 6c 01 4d c7 2d 6f 8c cd 1e d9 2a ce 1d 41 f0 d8 de 89 57");
	    runTestVector(P, S, 4096, 20, "4b 00 79 01 b7 65 48 9a be ad 49 d9 26 f7 21 d0 65 a4 29 c1");
	    runTestVector(P, S, 16777216, 20, "ee fe 3d 61 cd 4d a4 e4 e9 94 5b 3d 6b a2 15 8c 26 34 e9 84");
	    runTestVector(
	            "passwordPASSWORDpassword".toCharArray(),
	            "saltSALTsaltSALTsaltSALTsaltSALTsalt".getBytes(StandardCharsets.UTF_8),
	            4096, 25, "3d 2e ec 4f e4 1c 84 9b 80 c8 d8 36 62 c0 e4 4a 8b 29 1a 96 4c f2 f0 70 38");
		runTestVector(
				"pass&#92;&#48;word".toCharArray(),
				"sa&#92;&#48;lt".getBytes(StandardCharsets.UTF_8),
				4096, 16, "56 fa 6a a7 55 48 09 9d cc 37 d7 f0 34 25 e0 c3");

	}
	public static void main(String[] args) {
		RFC6070();
	}
}
