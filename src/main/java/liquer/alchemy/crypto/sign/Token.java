package liquer.alchemy.crypto.sign;

import liquer.alchemy.athanor.Attr;

public class Token {
	@Attr
	Long timeInMillis;

	@Attr
	Long expiresInMillis;

	@Attr
	int random;
}
