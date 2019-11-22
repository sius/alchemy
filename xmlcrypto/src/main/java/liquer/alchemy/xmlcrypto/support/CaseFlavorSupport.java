package liquer.alchemy.xmlcrypto.support;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide case conversion for input Strings using RegularExpressions.
 * To prevent ReDoS (Regular Expression Denial of Service) attacks
 * the max input length is restricted to 128 characters.
 */
public final class CaseFlavorSupport {

    private static final String EMPTY;
    private static final int MAX_INPUT_LENGTH = 128;
    private static final Pattern CAMEL_CASE_PATTERN;
    private static final Pattern ENSURE_TRAIN_CASE_PATTERN;
    private static final Pattern SEPARATED_CASE_PATTERN;
    private static final Pattern PUNCTUATION_PATTERN;

    static {
        EMPTY = "";
        CAMEL_CASE_PATTERN = Pattern.compile("([\\s\\p{Punct}]+|^)([\\p{L}])");
        ENSURE_TRAIN_CASE_PATTERN = Pattern.compile("([\\p{L}]+)");
        SEPARATED_CASE_PATTERN = Pattern.compile("([\\s\\p{Punct}\\p{Lu}]+)");
        PUNCTUATION_PATTERN =  Pattern.compile("\\p{Punct}+");
    }

    private CaseFlavorSupport() { }

    public static String toCamelCase(String input) {
        return toCamelCase(input, Locale.US);
    }

    public static String toCamelCase(String input, Locale locale) {
        String ret = EMPTY;
        if (!StringSupport.isNullEmptyOrBlank(input)) {

            input = input.substring(0, Math.min(MAX_INPUT_LENGTH, input.length()));

            Matcher m = CAMEL_CASE_PATTERN.matcher(input);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                m.appendReplacement(buffer, m.group(2).toLowerCase(ensure(locale)));
                while (m.find()) {
                    m.appendReplacement(buffer, m.group(2).toUpperCase(ensure(locale)));
                }
                m.appendTail(buffer);
                ret = buffer.toString();
            }
        }
        return ret;
    }

    public static <T> T identity(T input) {
        return input;
    }

    public static String toPascalCase(String input) {
        return toTitleCase(toCamelCase(input));
    }

    public static String toPascalCase(String input, Locale locale) {
        return toTitleCase(toCamelCase(input, locale));
    }

    public static String toSnakeCase(String input) {
        return toSeparatedLowerCase(input, '_', Locale.US);
    }

    public static String toSnakeCase(String input, Locale locale) {
        return toSeparatedLowerCase(input, '_', locale);
    }

    public static String toLispCase(String input) {
        return toSeparatedLowerCase(input, '-', Locale.US);
    }

    public static String toLispCase(String input, Locale locale) {
        return toSeparatedLowerCase(input, '-', locale);
    }

    public static String toKebabCase(String input) {
        return toLispCase(input);
    }

    public static String toKebabCase(String input, Locale locale) {
        return toLispCase(input, locale);
    }

    public static String toTrainCase(String input) {
        return toSeparatedTitleCase(input, '-');
    }

    public static String toJavaGetter(String input) {
        return toJavaGetter(input, Locale.US);
    }

    public static String toJavaGetter(String input, Locale locale) {
        return toPrefixedPascalCase("get", input, ensure(locale));
    }

    public static String toPrefixedPascalCase(String prefix, String input) {
        return toPrefixedPascalCase(prefix, input, Locale.US);
    }

    public static String toPrefixedPascalCase(String prefix, String input, Locale locale) {
        return String.format("%1$s%2$s", prefix, toPascalCase(input, ensure(locale)));
    }

    public static Locale ensure(Locale locale) {
        return locale == null ? Locale.US : locale;
    }

    public static String toSeparatedLowerCase(String input, char sep, Locale locale) {
        String ret = EMPTY;
        if (StringSupport.notNullEmptyOrBlank(input)) {

            input = input.substring(0, Math.min(MAX_INPUT_LENGTH, input.length()));

            Matcher m = SEPARATED_CASE_PATTERN.matcher(input);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                String prefix = m.start() > 0 ? String.valueOf(sep) : "";
                m.appendReplacement(buffer, prefix + m.group(1).toLowerCase(ensure(locale)));
                while (m.find()) {
                    m.appendReplacement(buffer, sep + m.group(1).toLowerCase(ensure(locale)));
                }
                m.appendTail(buffer);
                ret = PUNCTUATION_PATTERN.matcher(buffer.toString()).replaceAll(Character.toString(sep));
            } else {
                ret = input.toLowerCase(locale);
            }
        }
        return ret;
    }

    public static String toSeparatedTitleCase(String input, char sep) {
        String ret = EMPTY;
        if (!StringSupport.isNullEmptyOrBlank(input)) {
            Matcher m = SEPARATED_CASE_PATTERN.matcher(input);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                String prefix = m.start() > 0 ? String.valueOf(sep) : "";
                m.appendReplacement(buffer, prefix + toTitleCase(m.group(1)));
                while (m.find()) {
                    m.appendReplacement(buffer, sep + toTitleCase(m.group(1)));
                }
                m.appendTail(buffer);

                ret = PUNCTUATION_PATTERN.matcher(buffer.toString()).replaceAll(Character.toString(sep));

                m = ENSURE_TRAIN_CASE_PATTERN.matcher(ret);

                buffer = new StringBuffer();
                while (m.find()) {
                    m.appendReplacement(buffer, toTitleCase(m.group(1)));
                }
                ret = buffer.toString();

            } else {
                ret = toTitleCase(input);
            }
        }
        return ret;
    }

    public static String toTitleCase(String input) {
        if (input == null || input.trim().length() == 0) {
            return "";
        }
        Character first = Character.toTitleCase(input.charAt(0));
        return first + (input.length() > 1 ? input.substring(1) : "");
    }
}
