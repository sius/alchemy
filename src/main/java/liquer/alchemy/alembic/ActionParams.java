package liquer.alchemy.alembic;

import java.util.Map;

public class ActionParams extends TypedMap {

	/* common TypedMap keys */
	public static final String $controller = ":controller";
	public static final String $action = ":action";
	public static final String $id = ":id";
	public static final String $namespace = ":namespace";
	public static final String $scope = ":scope";
	public static final String $format = ":format";
	public static final String $path = ":path";
	public static final String $constraint = ":constraint";
	public static final String $locale = ":locale";
	public static final String $template = ":template";
	public static final String $partial = ":partial";
	public static final String $layout = ":layout";
	public static final String $handler = ":handler";
	public static final String $data = ":data";
	public static final String $prefetched = ":prefetched";
	public static final String $rebase = ":rebase";

	public String get$Id() {
		return stringValue($id, null);
	}
	public String get$Path() {
		return stringValue($path, null);
	}
	public String get$Controller() {
		return stringValue($controller, null);
	}
	public String get$Format() {
		return stringValue($format, null);
	}
	public String get$Action() {
		return stringValue($action, null);
	}
	public String get$Namespace() {
		return stringValue($namespace, null);
	}
	public String get$Locale() {
		return stringValue($locale, null);
	}
	public Object get$Data() {
		return get($data);
	}

	public ActionParams() {
	}
	public ActionParams(Map<String, Object> map) {
		super(map);
	}
}
