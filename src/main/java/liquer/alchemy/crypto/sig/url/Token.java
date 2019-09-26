package liquer.alchemy.crypto.sig.url;

import liquer.alchemy.athanor.reflect.Attr;

public class Token {
	@Attr
	Long timeInMillis;

	@Attr
	Long expiresInMillis;

	@Attr
	int random;
}
