package liquer.alchemy.alembic;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static liquer.alchemy.alembic.StringSupport.isNullEmptyOrBlank;

public class CaseFlavorSupport {

    private CaseFlavorSupport() {}
    private static final String CAMEL_CASE_REGEX = "([\\s\\p{Punct}]+|^)([\\p{L}])";
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile(CAMEL_CASE_REGEX);
    private static final String ENSURE_TRAIN_CASE_REGEX = "([\\p{L}]+)";
    private static final Pattern ENSURE_TRAIN_CASE_PATTERN = Pattern.compile(ENSURE_TRAIN_CASE_REGEX);

    public static Locale ensureLocale(Locale locale) {
        return locale == null ? Locale.getDefault() : locale;
    }

    public static String identity(String input) {
        return input;
    }

    public static String toCamelCase(String input) {
        return toCamelCase(input, Locale.getDefault());
    }

    public static String toCamelCase(String input, Locale locale) {
        String ret = "";
        if (!isNullEmptyOrBlank(input)) {
            Matcher m = CAMEL_CASE_PATTERN.matcher(input);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                m.appendReplacement(buffer, m.group(2).toLowerCase(ensureLocale(locale)));
                while (m.find()) {
                    m.appendReplacement(buffer, m.group(2).toUpperCase(ensureLocale(locale)));
                }
                m.appendTail(buffer);
                ret = buffer.toString();
            }
        }
        return ret;
    }


    public static String toPascalCase(String input) {
        return toTitleCase(toCamelCase(input));
    }

    public static String toPascalCase(String input, Locale locale) {
        return toTitleCase(toCamelCase(input, locale));
    }

    public static String toSnakeCase(String input) {
        return toSeparatedLowerCase(input, '_', Locale.getDefault());
    }

    public static String toSnakeCase(String input, Locale locale) {
        return toSeparatedLowerCase(input, '_', locale);
    }

    public static String toLispCase(String input) {
        return toSeparatedLowerCase(input, '-', Locale.getDefault());
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
        return toJavaGetter(input, Locale.getDefault());
    }

    public static String toJavaGetter(String input, Locale locale) {
        return toPrefixedPascalCase("get", input, ensureLocale(locale));
    }

    public static String toPrefixedPascalCase(String prefix, String input) {
        return toPrefixedPascalCase(prefix, input, Locale.getDefault());
    }

    public static String toPrefixedPascalCase(String prefix, String input, Locale locale) {
        return String.format("%1$s%2$s", prefix, toPascalCase(input, ensureLocale(locale)));
    }


    private static final String SEPARATED_CASE_REGEX = "([\\s\\p{Punct}\\p{Lu}]+)";
    private static final Pattern SEPARATED_CASE_PATTERN = Pattern.compile(SEPARATED_CASE_REGEX);

    public static String toSeparatedLowerCase(String value, char sep, Locale locale) {
        String ret = "";
        if (!isNullEmptyOrBlank(value)) {
            Matcher m = SEPARATED_CASE_PATTERN.matcher(value);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                String prefix = m.start() > 0 ? String.valueOf(sep) : "";
                m.appendReplacement(buffer, prefix + m.group(1).toLowerCase(ensureLocale(locale)));
                while (m.find()) {
                    m.appendReplacement(buffer, sep + m.group(1).toLowerCase(ensureLocale(locale)));
                }
                m.appendTail(buffer);
                Pattern p = Pattern.compile("\\p{Punct}+");
                ret = p.matcher(buffer.toString()).replaceAll(Character.toString(sep));
            } else {
                ret = value.toLowerCase(locale);
            }
        }
        return ret;
    }
    public static String toSeparatedUpperCase(String value, char sep, Locale locale) {
        String ret = "";
        if (!isNullEmptyOrBlank(value)) {
            Matcher m = SEPARATED_CASE_PATTERN.matcher(value);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                String prefix = m.start() > 0 ? String.valueOf(sep) : "";
                m.appendReplacement(buffer, prefix + m.group(1).toUpperCase(ensureLocale(locale)));
                while (m.find()) {
                    m.appendReplacement(buffer, sep + m.group(1).toUpperCase(ensureLocale(locale)));
                }
                m.appendTail(buffer);
                Pattern p = Pattern.compile("\\p{Punct}+");
                ret = p.matcher(buffer.toString()).replaceAll(Character.toString(sep));
            } else {
                ret = value.toUpperCase(locale);
            }
        }
        return ret;
    }
    public static String toSeparatedTitleCase(String value, char sep) {
        String ret = "";
        if (!isNullEmptyOrBlank(value)) {
            Matcher m = SEPARATED_CASE_PATTERN.matcher(value);
            StringBuffer buffer = new StringBuffer();
            if (m.find()) {
                String prefix = m.start() > 0 ? String.valueOf(sep) : "";
                m.appendReplacement(buffer, prefix + toTitleCase(m.group(1)));
                while (m.find()) {
                    m.appendReplacement(buffer, sep + toTitleCase(m.group(1)));
                }
                m.appendTail(buffer);
                Pattern p = Pattern.compile("\\p{Punct}+");
                ret = p.matcher(buffer.toString()).replaceAll(Character.toString(sep));
                m = ENSURE_TRAIN_CASE_PATTERN.matcher(ret);
                buffer = new StringBuffer();
                while (m.find()) {
                    m.appendReplacement(buffer, toTitleCase(m.group(1)));
                }
                ret = buffer.toString();
            } else {
                ret = toTitleCase(value);
            }
        }
        return ret;
    }
    public static String toTitleCase(String input) {
        if (isNullEmptyOrBlank(input)) {
            return "";
        }
        char first = Character.toTitleCase(input.charAt(0));
        return first + (input.length() > 1 ? input.substring(1) : "");
    }
}
