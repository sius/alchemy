package liquer.alchemy.crypto.sig.v4;

import liquer.alchemy.support.StringSupport;
import liquer.alchemy.alembic.Tuple.Tuple2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CanonicalURI {

	private URI originalURI;
	public CanonicalURI(URI originalURI) {
		this.originalURI = originalURI;
	}
	public URI getOriginalURI() {
		return originalURI;
	}
	/**
	 * @return the URI encoded ordered query string
	 */
	public String getCanonicalQueryString() {
		if (originalURI.getQuery() == null) {
			return "";
		}
		Function<String, Boolean> predicate = s -> !StringSupport.isNullEmptyOrBlank(s);
		Comparator<Tuple2<String, String>> comparator = (t1, t2) -> {
			if (t1 == null) {
				return -1;
			}
			if (t2 == null) {
				return 1;
			}
			return t1._1.compareTo(t2._1);
		};

		return Stream.of(originalURI.getQuery().split("&"))
				.filter(predicate::apply)
				.map(x -> {
					String[] result = x.split("=", 2);
					return new Tuple2<>(
						StringSupport.uriEncode(result[0], true),
						(result.length == 1 ? "" : StringSupport.uriEncode(result[1], true)) );
				})
				.sorted(comparator)
				.map(t -> String.format("%1$s=%2$s", t._1, t._2))
				.collect(Collectors.joining("&"));
	}
	public String getQueryString() {
		if (originalURI.getQuery() == null) {
			return "";
		}
		Function<String, Boolean> predicate = s -> !StringSupport.isNullEmptyOrBlank(s);

		return Stream.of(originalURI.getQuery().split("&"))
				.filter(predicate::apply)
				.map(x -> {
					String[] result = x.split("=", 2);
					return new Tuple2<>(
						StringSupport.uriEncode(result[0], true),
						(result.length == 1 ? "" : StringSupport.uriEncode(result[1], true)) );
				})
				.map(t -> String.format("%1$s=%2$s", t._1, t._2))
				.collect(Collectors.joining("&"));
	}
	public boolean isDefaultPort() {
		return (originalURI.getPort() == -1) ||
				(originalURI.getScheme().equals("http") && originalURI.getPort() == 80) ||
				(originalURI.getScheme().equals("https") && originalURI.getPort() == 443);
	}
	public URI getEncodedURI() throws URISyntaxException {
		StringBuilder ret = new StringBuilder();
		ret.append(originalURI.getScheme());
		ret.append("://");
		ret.append(originalURI.getHost());
		if (!isDefaultPort()) {
			ret.append(String.format(":%1$d", originalURI.getPort()));
		}
		ret.append(originalURI.getPath());
		String queryString = getQueryString();
		if (queryString.length() > 0) {
			ret.append(String.format("?%1$s", queryString));
		}
		String fragment = originalURI.getFragment();
		if (fragment != null && fragment.length() > 0) {
			ret.append(String.format("#%1$s", fragment));
		}
		return new URI(ret.toString());
	}
	@Override public String toString() {
		return originalURI == null ? "" : originalURI.toString();
	}
}
