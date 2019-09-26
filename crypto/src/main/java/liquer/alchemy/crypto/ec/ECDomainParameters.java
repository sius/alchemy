package liquer.alchemy.crypto.ec;

import liquer.alchemy.athanor.reflect.Attr;
import liquer.alchemy.athanor.Tuple.Tuple2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public final class ECDomainParameters {

	private static final List<Integer> elementsOfP = Arrays.asList(192, 224, 256, 384, 521);

	private static class Log2Result extends Tuple2<Integer, Integer> {

		private Log2Result(Integer integer, Integer integer2) {
			super(integer, integer2);
		}
		@Attr("exp")
		public int getExp() {
			return _1;
		}
		@Attr("r")
		public int getR() {
			return _2;
		}
	}
	public static Tuple2<Integer, Integer> log2(final BigInteger n) {
		BigInteger right = n;
		BigInteger last = n;
		int i = 0;
		while ((right = right.shiftRight(1)).compareTo(BigInteger.ZERO) > 0) { i++; System.out.println(right);}
		return new Log2Result(i, right.intValue());
	}


	private ECNickname nickname;

	@Attr
	private BigInteger p;
	@Attr
	private BigInteger a;
	@Attr
	private BigInteger b;
	@Attr
	private BasePoint G;
	@Attr
	private BigInteger n;
	@Attr
	private BigInteger h;

	public ECDomainParameters() {
		this(null,null, null,null, null, null, null);
	}
	public ECDomainParameters(String nickname, BigInteger p, BigInteger a, BigInteger b, BasePoint G, BigInteger n, BigInteger h) {
		this.setNickname(nickname);
		this.p = p;
		this.a = a;
		this.b = b;
		this.G = G;
		this.n = n;
		this.h = h;
	}

	@Attr
	private void setNickname(String nickname) {
		this.nickname = new ECNickname(nickname);
	}
	public ECNickname getNickname() {
		return nickname;
	}

	public BigInteger getP() {
		return p;
	}
	public BigInteger getA() {
		return a;
	}
	public BigInteger getB() {
		return b;
	}
	public BasePoint getG() {
		return G;
	}
	public BigInteger getN() {
		return n;
	}
	public BigInteger getH() {
		return h;
	}
}
