package liquer.alchemy.crypto.sig.v4;

import liquer.alchemy.crypto.CryptoLimericks;
import liquer.alchemy.support.StringSupport;

import java.net.URI;

public class CanonicalRequest {

	final public static String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

	private CanonicalHeaders canonicalHeaders;
	private String httpRequestMethod;
	private CanonicalURI canonicalURI;
	private String algorithm;
	private String requestPayload;
	private boolean unsigned;

	public static CanonicalRequest getUnsignedPayloadRequest(URI uri, CanonicalHeaders canonicalHeaders, String method) {
		return new CanonicalRequest(uri, canonicalHeaders, "SHA-256", UNSIGNED_PAYLOAD, method, true);
	}
	public CanonicalRequest(URI uri, CanonicalHeaders canonicalHeaders, String payload) {
		this(uri, canonicalHeaders, "SHA-256", payload, "GET", false);
	}
	public CanonicalRequest(URI uri, CanonicalHeaders canonicalHeaders, String algorithm, String payload, String method, boolean unsigned) {
		this.canonicalURI = new CanonicalURI(uri);
		this.canonicalHeaders = canonicalHeaders == null ? new CanonicalHeaders(uri.getHost()) : canonicalHeaders;
		this.httpRequestMethod = method;
		this.algorithm = (StringSupport.isNullEmptyOrBlank(algorithm) ? "SHA-256" : algorithm).toUpperCase();
		this.requestPayload = (payload == null ? "" : payload);
		this.unsigned = unsigned;
	}
	public CanonicalURI getCanonicalURI() { return canonicalURI; }
	public CanonicalHeaders getCanonicalHeaders() { return canonicalHeaders; }
	public String getHttpRequestMethod() { return httpRequestMethod; }
	public String getAlgorithm() { return algorithm; }
	public String getRequestPayload() { return requestPayload; }
	public boolean isUnsigned() { return unsigned; }

	@Override public String toString() {
        return
            httpRequestMethod + "\n" +
            canonicalURI.getOriginalURI().getPath() + "\n" +
            canonicalURI.getCanonicalQueryString() + "\n" +
            canonicalHeaders + "\n" +
            canonicalHeaders.getSignedHeaders() + "\n" +
            (unsigned ? requestPayload : StringSupport.hexEncode(CryptoLimericks.hash(requestPayload, algorithm, false)));
	}
}
