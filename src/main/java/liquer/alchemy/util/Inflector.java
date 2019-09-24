package liquer.alchemy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Inflector uses 4 kinds of inflection rules:
 * uncountable, irregular, plural and singular rules.

 * @see <a href="http://www.javapractices.com/topic/TopicAction.do?Id=80">http://www.javapractices.com/topic/TopicAction.do?Id=80</a>
 */
public final class Inflector {

	/* inflection rules */
	private final List<String> uncountables = new ArrayList<>();
	private final Map<String, String> sp_irregulars = new HashMap<>();
	private final Map<String, String> ps_irregulars = new HashMap<>();
	private final Map<Pattern, String> singular_map = new HashMap<>();
	private final Map<Pattern, String> plural_map = new HashMap<>();
	private final List<Pattern> singulars = new ArrayList<>();
	private final List<Pattern> plurals = new ArrayList<>();
	
	/**
	 * initializes the inflector with a default inflection ruleSet for english
	 * @return the Inflector
	 */
	public static Inflector getInstance() {
		return getInstance(true);
	}
	/**
	* @param flag - true initializes the inflector with a default inflection ruleSet for english
	* @return the Inflector
	*/
	public static Inflector getInstance(boolean flag) {
		Inflector inflector =  new Inflector();
		if (flag) {
			Inflector.initInflector(inflector);
		}
		return inflector;
	}
	private static void initInflector(Inflector inflector) {
		inflector.inflectUncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep", "news");
		inflector.inflectIrregular("person", "people");
		inflector.inflectIrregular("child", "children");
		inflector.inflectIrregular("sex", "sexes");
		inflector.inflectIrregular("move", "moves");
		inflector.inflectIrregular("shoe", "shoes");
		
		inflector.inflectPlural("$", "s");
		inflector.inflectPlural("(?i)s$", "ss");
		inflector.inflectPlural("(?i)(stimul|hippopotam|octop|vir|syllab|foc|alumn|fung|radi)us$", "$1i");
		inflector.inflectPlural("(?i)(buffal|tomat)o$", "$1oes");
		inflector.inflectPlural("(?i)([ti])um$", "$1a");
		inflector.inflectPlural("(?i)sis$", "ses");
		inflector.inflectPlural("(?i)(?:([^f])fe|([lr])f)$", "$1$2ves");
		inflector.inflectPlural("(?i)(hive|tive)$", "$1s");
		inflector.inflectPlural("(?i)([^aeiouy]|qu)y$", "$1ies");
		inflector.inflectPlural("(?i)(x|ch|ss|sh)$", "$1es");
		inflector.inflectPlural("(?i)(cris|ax|test)is$", "$1es");
		inflector.inflectPlural("(?i)(alias|status)$", "$1es");
		inflector.inflectPlural("(?i)(bu|len)s$", "$1ses");
		inflector.inflectPlural("(?i)(matr|vert|ind)ix|ex$", "$1ices");
		inflector.inflectPlural("(?i)([m|l])ouse$", "$1ice");
		inflector.inflectPlural("(?i)^(ox)$", "$1en");
		inflector.inflectPlural("(?i)(quiz)$", "$1zes");
		inflector.inflectPlural("(?i)(phenomen|criteri)on$", "$1a");
		inflector.inflectPlural("(?i)^(?!(.*hu|.*ger|.*sha))(.*)(wom|m)an$", "$2$3en");
		inflector.inflectPlural("(?i)(medi|curricul|bacteri)um$", "$1a");
		inflector.inflectPlural("(?i)(nebul|formul|vit|vertebr|alg|alumn)a$", "$1ae");
		
		inflector.inflectSingular("(?i)s$", "");
		inflector.inflectSingular("(?i)ss$", "s");
		inflector.inflectSingular("(?i)(stimul|hippopotam|octop|vir|syllab|foc|alumn|fung|radi)i$", "$1us");
		inflector.inflectSingular("(?i)(o)es$", "$1");
		inflector.inflectSingular("(?i)([ti])a$", "$1um");
		inflector.inflectSingular("(?i)ses$", "sis");
		inflector.inflectSingular("(?i)(kni)ves$", "$1fe");
		inflector.inflectSingular("(?i)([lr])ves$", "$1f");
		inflector.inflectSingular("(?i)(hive|tive)s$", "$1");
		inflector.inflectSingular("(?i)([^aeiouy]|qu)ies$", "$1y");
		inflector.inflectSingular("(?i)(x|ch|ss|sh)es$", "$1");
		inflector.inflectSingular("(?i)(cris|ax|test)es$", "$1is");
		inflector.inflectSingular("(?i)(alias|status)es$", "$1");
		inflector.inflectSingular("(?i)(bus|lens)es$", "$1");
		inflector.inflectSingular("(?i)(matr)ices$", "$1ix");
		inflector.inflectSingular("(?i)(vert|ind)ices$", "$1ex");
		inflector.inflectSingular("(?i)([m|l])ice$", "$1ouse");
		inflector.inflectSingular("(?i)^(ox)en$", "$1");
		inflector.inflectSingular("(?i)(quiz)zes$", "$1");
		inflector.inflectSingular("(?i)(phenomen|criteri)a$", "$1on");
		inflector.inflectSingular("(?i)(.*)(wo|m)en$", "$1$2an");
		inflector.inflectSingular("(?i)(medi|curricul|bacteri)a$", "$1um");
		inflector.inflectSingular("(?i)(nebula|formula|vita|vertebra|alga|alumna)e$", "$1");
	}
	
	private Inflector() { }
	/**
	* last rule in applies first; rules added hides earlier defined rules
	* @param singularPattern singular pattern
	* @param replacement replacement
	*/
	public void inflectPlural(String singularPattern, String replacement) {
		Pattern p = Pattern.compile(singularPattern);
		singular_map.put(p, replacement);
		singulars.add(0, p);
	}
	/**
	* last rule in applies first; rules added hides earlier defined rules
	* @param pluralPattern plural pattern
	* @param replacement replacement
	*/
	public void inflectSingular(String pluralPattern, String replacement) {
		Pattern p = Pattern.compile(pluralPattern);
		plural_map.put(p, replacement);
		plurals.add(0, p);
	}
	/**
	* the irregular rule will be applied always second
	* @param singular singular
	* @param plural plural
	*/
	public void inflectIrregular(String singular, String plural) {
		sp_irregulars.put(singular, plural);
		ps_irregulars.put(plural, singular);
	}
	/**
	* the uncountable rule will be applied always first
	* @param nouns nouns
	*/
	public void inflectUncountable(String... nouns) {
		for (String noun : nouns) {
			if (!uncountables.contains(noun)) {
				uncountables.add(noun);
			}
		}
	}
	/**
	* the pluralize recipe applies the inflection rules in the following order:
	* uncountable, irregular, plural
	* @param noun singular noun
	* @return the plural value
	*/
	public String pluralize(String noun) {
		String ret = noun;
		if (!uncountables.contains(noun)) {
			if (sp_irregulars.containsKey(noun)) {
				ret = sp_irregulars.get(noun);
			} else {
				for (Pattern p : singulars) {
					Matcher m;
					if ((m = p.matcher(noun)).find()) {
						ret = m.replaceFirst(singular_map.get(p));
						break;
					}
				}
			}
		}
		return ret;
	}
	/**
	 * uncountable returns always true
	 * @param noun noun
	 * @return true if singular
	 */
	public boolean isSingular(String noun) {
		boolean ret = false;
		if (uncountables.contains(noun)) {
			ret = true;
		} else if (sp_irregulars.containsKey(noun)) {
			ret = true;
		} else {
			for (Pattern p : singulars) {
				if (p.matcher(noun).find()) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	/**
	 * uncountable returns always true
	 * @param noun noun
	 * @return true if plural
	 */
	public boolean isPlural(String noun) {
		boolean ret = false;
		if (uncountables.contains(noun)) {
			ret = true;
		} else if (ps_irregulars.containsKey(noun)) {
			ret = true;
		} else {
			for (Pattern p : plurals) {
				if (p.matcher(noun).find()) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	* the singularize recipe applies the inflection rules in the following order:
	* uncountable, irregular, singular
	* @param noun noun
	* @return the singular value
	*/
	public String singularize(String noun) {
		String ret = noun;
		if (!uncountables.contains(noun)) {
			if (ps_irregulars.containsKey(noun)) {
				ret = ps_irregulars.get(noun);
			} else {
				for (Pattern p : plurals) {
					Matcher m ;
					if ((m = p.matcher(noun)).find()) {
						ret = m.replaceFirst(plural_map.get(p));
						break;
					}
				}
			}
		}
		return ret;
	}
}

