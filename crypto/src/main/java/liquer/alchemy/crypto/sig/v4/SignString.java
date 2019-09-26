package liquer.alchemy.crypto.sig.v4;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.alembic.StringSupport;

import java.net.URI;
import java.net.URISyntaxException;

public class SignString {
	public final static String SCHEME = "AWS4";
	private String scheme;
	private CredentialScope credentialScope;
	private String requestTimeStamp;
	private CanonicalRequest canonicalRequest;
	private String hashAlgorithm;
	private String hmacAlgorithm;

	public SignString(CredentialScope credentialScope, CanonicalRequest canonicalRequest) {
		this(SCHEME, credentialScope, canonicalRequest);
	}
	public SignString(String scheme, CredentialScope credentialScope, CanonicalRequest canonicalRequest) {
		this.scheme = StringSupport.isNullEmptyOrBlank(scheme) ? SCHEME : scheme.trim();
		this.credentialScope = credentialScope;
		this.requestTimeStamp = SignV4Utils.getS4TimeStamp(credentialScope.getDate());
		this.canonicalRequest = canonicalRequest;
		this.hashAlgorithm = canonicalRequest.getAlgorithm().replaceAll("-", "");
		this.hmacAlgorithm = "Hmac" + hashAlgorithm.replaceAll("-", "");

	}
	public String getScheme() { return scheme; }

	/**
	 * <code>yyyyMMdd'T'HHmmss'Z'</code> formatted DateTime String
	 */
	public String getRequestTimeStamp() { return requestTimeStamp; }
	public CredentialScope getCredentialScope() { return credentialScope; }
	public CanonicalRequest getCanonicalRequest() { return canonicalRequest; }
	public String getHashAlgorithm() { return hashAlgorithm; }
	public String getHmacAlgorithm() { return hmacAlgorithm;  }

	@Override public String toString() {

        return
            scheme + "-HMAC-" + hashAlgorithm.toUpperCase() + "\n" +
            requestTimeStamp + "\n" +
            credentialScope + "\n" +
            StringSupport.hexEncode(CryptoLimericks.hash(canonicalRequest.toString(), canonicalRequest.getAlgorithm(), false));
	}
	public String getSignature(String secretKey) {
		return getSignature(SignV4Utils.getBytes(secretKey, null));
	}
	public String getSignature(byte[] secretKey) {
		return StringSupport.hexEncode(CryptoLimericks.sign(toString(), hmacAlgorithm, SignV4Utils.getS4SignatureKey(hmacAlgorithm, credentialScope, secretKey)));
	}
	public URI getSignedUri(String signatureParameter, String secretKey) throws URISyntaxException {
		return getSignedUri(signatureParameter, SignV4Utils.getBytes(secretKey, null));
	}
	public URI getSignedUri(String signatureParameter, byte[] secretKey) throws URISyntaxException {
		return new URI(String.format("%1$s&%2$s=%3$s", canonicalRequest.getCanonicalURI().getEncodedURI().toString(), signatureParameter, getSignature(secretKey)));
	}
}
