package liquer.alchemy.crypto.jwk;

import liquer.alchemy.collection.TypedMap;
import liquer.alchemy.json.Json;

import java.util.Map;

public class X509CertificateChain extends TypedMap {

	public X509CertificateChain(Map<String, Object> map) {
		this.putAll(map);
	}

	@Override
	public String toString() {
		return Json.stringify(this, 2);
	}

}
