package liquer.alchemy.crypto.ec;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ECNickname {

	private Pattern nicknamePatternOverFp = Pattern.compile("secp(192|224|256|384|521)(k|r)(1)");
	private Pattern nicknamePatternOverF2m = Pattern.compile("sect(163|233|239|283|409|571)(k|r)(1|2)");

	private String nickname;
	private boolean overFp;
	private boolean overF2m;
	private int size;
	private String kr;
	private int n;

	public ECNickname(String nickname) {
		this.nickname = nickname;
		Matcher mFp = nicknamePatternOverFp.matcher(nickname);
		if (mFp.matches()) {
			overFp = true;
			size = Integer.valueOf(mFp.group(1));
			kr = mFp.group(2);
			n = Integer.valueOf(mFp.group(3));
		} else {
			Matcher mF2m = nicknamePatternOverF2m.matcher(nickname);
			if (mF2m.matches()) {
				overF2m = true;
				size = Integer.valueOf(mFp.group(1));
				kr = mFp.group(2);
				n = Integer.valueOf(mF2m.group(3));
			} else {
				throw new InvalidParameterException(nickname);
			}
		}
	}

	@Override
	public String toString() {
		return this.nickname;
	}
}
