package liquer.alchemy.util;

import liquer.alchemy.athanor.FurnaceConfig;
import liquer.alchemy.athanor.FurnaceManager;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pedantic {
	
	static final FurnaceConfig CONFIG = FurnaceManager.getInstance().getConfig();
	
	static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static final String DIGIT = "0123456789";
	static final String PUNCT =  ",;:$&+=";
	static final String SPACE = " ";
	
	static final String ALPHANUM = ALPHA + DIGIT;
	static final String UNRESERVED = ALPHANUM + "_-!.~'()*";
	static final String UNRESERVED_RFC3986 = ALPHANUM + "_-~.";
	static final String RESERVED = PUNCT + "?/[]@";
	static final String EMPTY = "";
	
	
	static final String OTHER_ISO_CONTROL = String.valueOf(
		new char[] {
			'\u0000', '\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\u0007',
			'\u0008', '\u0009', '\n'   ,'\u000B','\u000C','\r'    ,'\u000E','\u000F',
			'\u0010', '\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017',
			'\u0018', '\u0019','\u001A','\u001B','\u001C','\u001D','\u001E','\u001F',
			'\u007F', 
			'\u0080','\u0081','\u0082','\u0083','\u0084','\u0085','\u0086','\u0087',
			'\u0088','\u0089','\u008A','\u008B','\u008C','\u008D','\u008E','\u008F',
			'\u0090','\u0091','\u0092','\u0093','\u0094','\u0095','\u0096','\u0097',
			'\u0098','\u0099','\u009A','\u009B','\u009C','\u009D','\u009E','\u009F'});
	
	static final char[] HEX_ARRAY_UPPER = "0123456789ABCDEF".toCharArray();
	static final char[] HEX_ARRAY_LOWER = "0123456789abcdef".toCharArray();
	static final char[] FORBIDDEN_FILENAME_CHARACTERS;
	
	static {
		char[] forbidden = "\\/:*?\"<>|".toCharArray();
		Arrays.sort(forbidden);
		FORBIDDEN_FILENAME_CHARACTERS = forbidden;
	}



	private Pedantic() { }
	
	/**
     * @param value input
     * @return true if value is null or toString value is empty
     */
    public static boolean isNullOrEmpty(Object value) {
        return isNullOrEmpty(stringify(value));
    }
    
	/**
     * @param value input
     * @return <tt>true</tt> if value is null or empty
     */
    public static boolean isNullOrEmpty(String value) {
        return (value == null || value.length() == 0);
    }
	
	
    /**
     * @param value input
     * @return <tt>true</tt> if value is null or toString value is empty or blanks only
     */
    public static boolean isNullEmptyOrBlank(Object value) {
        return isNullEmptyOrBlank(stringify(value));
    }
	    
    /**
     * @param value input
     * @return <tt>true</tt> if value is null, empty or blanks only
     */
    public static boolean isNullEmptyOrBlank(String value) {
        return (value == null || value.trim().length() == 0);
    }
    
    public static boolean notNullOrEmpty(String value) {
    	return !isNullOrEmpty(value);
    }
    
    public static boolean notNullOrEmpty(Object value) {
    	return !isNullOrEmpty(stringify(value));
    }
    
    public static boolean notNullEmptyOrBlank(String value) {
    	return !isNullEmptyOrBlank(value);
    }
    
    public static boolean notNullEmptyOrBlank(Object value) {
    	return !isNullEmptyOrBlank(stringify(value));
    }
	    

    /**
     * @param value input
     * @return <code>String</code> value or <tt>""</tt>
     */
    public static String stringify(Object value) {
    	if (value == null) return "";
    	return value.toString();
    }
	    
      
    public static String quote(String input) {
    	return String.format("\"%1$s\"", input);
    }
    
    public static String multiply(String input, int times) {
    	if (input == null || input.isEmpty() || times <= 0) {
    		return "";
    	}
    	StringBuilder buf = new StringBuilder(input.length() * times);
    	for (int i = 0; i < times; i++) {
    		buf.append(input);
    	}
    	return buf.toString();
    }
    
    public static String multiply(char input, int times) {
    	int len = Math.max(times,  1);
    	StringBuilder buf = new StringBuilder(len);
    	for (int i = 0; i < len; i++) {
    		buf.append(input);
    	}
    	return buf.toString();
    }
    
    public static String trim(String value, String trim) {
        return trimEnd(trimStart(value, trim), trim);
    }
    
    public static String trimStart(String value, String trim) {
        if (isNullOrEmpty(value)) {
            return value;
        }
        if (isNullOrEmpty(trim)) {
            return value;
        }
        if (value.equals(trim)) {
            return EMPTY;
        }
        if (value.startsWith(trim)) {
            return value.substring(trim.length());
        }
        return value;
    }
    public static String trimEnd(String value, String trim) {
    	if (isNullOrEmpty(value)) {
            return value;
        }
        if (isNullOrEmpty(trim)) {
            return value;
        }
        if (value.equals(trim)) {
            return EMPTY;
        }
        if (value.endsWith(trim)) {
            return value.substring(0, value.length() - trim.length());
        }
        return value;
    }
    
    public static String trimStart(String value, char... array) {
    	if (value == null || value.length() == 0 || array.length == 0) return value;
    	int trim = 0;
    	Arrays.sort(array);
        for (int i = 0; i < value.length(); i++) {
        	if (Arrays.binarySearch(array, value.charAt(i)) > -1) {
        		trim = i+1;
        	} else {
        		break;
        	}
        }
        return value.substring(trim);
    }
	    
    public static String trimEnd(String value, char... array) {
    	if (value == null || value.length() == 0 || array.length == 0) return value;
    	Arrays.sort(array);
    	int i = value.length()-1;
    	int trim = value.length();
        for (; i > -1; i--) {
        	if (Arrays.binarySearch(array, value.charAt(i)) > -1) {
        		trim--;
        	} else {
        		break;
        	}
        }
        return value.substring(0, trim);
    }
	  
    public static String trim(String value, char... array) {
    	if (value == null || value.length() == 0 || array.length == 0) 
    		return value;
    	return trimEnd(trimStart(value, array), array);
    }
	    
    public static String cutStart(String value, String... prefixes) {
    	if (isNullOrEmpty(value) || prefixes == null)
    		return value;
    	for(String prefix : prefixes) {
    		if (value.indexOf(prefix) == 0) {
    			return value.substring(prefix.length());
    		} 
    	}
    	return value;
    }
    
    public static String cutEnd(String value, String... suffixes) {
    	if (isNullOrEmpty(value) || suffixes == null)
    		return value;
    	for(String suffix : suffixes) {
	    	int rpos = value.lastIndexOf(suffix);
	    	if (rpos == (value.length() - suffix.length())) {
	    		return value.substring(0, rpos);
	    	}
    	}
    	return value;
    }
	    
    public static String cut(String value, String... edges) {
    	if (value == null || value.length() == 0 || edges.length == 0) 
    		return value;
    	return cutEnd(cutStart(value, edges), edges);
    }
    
    public static String joinDistinct(String delimiter, Object... values) {
        return Stream.of(values).map(x -> trim(x.toString(), delimiter)).filter(Pedantic::notNullEmptyOrBlank)
         .collect(Collectors.joining(delimiter));
    }
    public static String joinUrl(Object... values) {
        return joinDistinct("/", values);
    }
    
	@SafeVarargs
	public static <T>String join(String seperator, T... words) {
    	if (words == null) return "";
    	seperator = seperator == null ? "" : seperator;
    	StringBuilder ret = new StringBuilder(words.length);
    	for(T w : words) {
    		ret.append(seperator);
    		ret.append(w != null ? w.toString() : "");
    		
    	}
    	if (words.length>0)
    		ret.delete(0, seperator.length());
    	return ret.toString();
    }

    public static String reverse(String value) {
    	if (value == null || value.length() == 0) return value;
    	StringBuilder buf = new StringBuilder();
        for (int i = value.length()-1 ; i > -1; i--) 
        	buf.append(value.charAt(i));
        return buf.toString();
    }
    
    public static String sanitizeStrings(String value) {
		return (value != null)
			? value.replace('\0', ' ').trim()
			: null;
	}
	    
    /**
     * @param charsetName charsetName
     * @return {@link Charset#forName(String)} or UTF-8
     */
    public static Charset ensureCharset(String charsetName) {
    	Charset ret = CONFIG.getCharset();
			if (charsetName != null) {
				try { ret = Charset.forName(charsetName); }
				catch (IllegalCharsetNameException | UnsupportedCharsetException ignore) {
					// no operation
				}
			}
			return ret;
    }
	    
    /**
     * 
     * @param value value
     * @param charsetName charsetName
     * @return encoded byte array or empty byte array if charArray is null or empty
     */
    public static byte[] getEncodedBytes(String value, String charsetName) {
    	return getEncodedBytes(value.toCharArray(), charsetName);
    }
	    
    /**
     * 
     * @param value value
     * @param charset charset
     * @return encoded byte array or empty byte array if charArray is null or empty
     */
    public static byte[] getEncodedBytes(String value, Charset charset) {
    	return getEncodedBytes(value.toCharArray(), charset);
    }
    
    /**
     * 
     * @param charArray charArray
     * @param charsetName charsetName
     * @return encoded byte array or empty byte array if charArray is null or empty
     */
    public static byte[] getEncodedBytes(char[] charArray, String charsetName) {
		return getEncodedBytes(charArray, ensureCharset(charsetName));
	}
    
    /**
     * 
     * @param charArray - encoded byte array
     * @param charset - UTF-8 if null
     * @return encoded byte array or empty byte array if charArray is null or empty
     */
    public static byte[] getEncodedBytes(char[] charArray, Charset charset) {
    	byte[] ret = new byte[0];
		if (charArray == null || charArray.length == 0)
			return ret;
		if (charset == null)
    		charset = CONFIG.getCharset();
		if (charset.canEncode()) {
			CharBuffer cbuf = CharBuffer.wrap(charArray);
			ByteBuffer bbuf = charset.encode(cbuf);
			ret = new byte[bbuf.limit()];
			bbuf.get(ret, 0, ret.length);
			cbuf.clear();
			bbuf.clear();
		}
		for (int i = 0; i < charArray.length; i++) {
			charArray[i] = '\0';
		}
		return ret;
    }
    
    /**
     * 
     * @param byteArray - encoded byte array
     * @param charsetName charsetName
     * @return decoded String or null if byteArray is null or empty
     */
    public static String decodeBytesToString(byte[] byteArray, String charsetName) {
		return decodeBytesToString(byteArray, ensureCharset(charsetName));
    }
    
    /**
     * 
     * @param byteArray - encoded byte array
     * @param charset - UTF-8 if null
     * @return decoded String or null if byteArray is null or empty
     */
    public static String decodeBytesToString(byte[] byteArray, Charset charset) {
    	if (byteArray == null || byteArray.length == 0)
    		return null;
    	if (charset == null)
    		charset = CONFIG.getCharset();
		return charset.decode(ByteBuffer.wrap(byteArray)).toString();
    }
    
    private static final String CAMEL_CASE_REGEX = "([\\s\\p{Punct}]+|^)([\\p{L}])";
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile(CAMEL_CASE_REGEX);
    private static final String ENSURE_TRAIN_CASE_REGEX = "([\\p{L}]+)";
    private static final Pattern ENSURE_TRAIN_CASE_PATTERN = Pattern.compile(ENSURE_TRAIN_CASE_REGEX);
    
    public static Locale ensureLocale(Locale locale) {
        return locale == null ? CONFIG.getLocale() : locale;
    }
    
    public static String identity(String input) {
        return input;
    }
    
    public static String toCamelCase(String input) {
        return toCamelCase(input, CONFIG.getLocale());
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
        return toSeparatedLowerCase(input, '_', CONFIG.getLocale());
    }
    
    public static String toSnakeCase(String input, Locale locale) {
        return toSeparatedLowerCase(input, '_', locale);
    }
    
    public static String toLispCase(String input) {
        return toSeparatedLowerCase(input, '-', CONFIG.getLocale());
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
        return toJavaGetter(input, CONFIG.getLocale());
    }
    
    public static String toJavaGetter(String input, Locale locale) {
        return toPrefixedPascalCase("get", input, ensureLocale(locale));
    }
    
    public static String toPrefixedPascalCase(String prefix, String input) {
        return toPrefixedPascalCase(prefix, input, CONFIG.getLocale());
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
   
    /**
     * Expanded placeholders ${...} with a {@link Properties} hashtable containing the expand values.
     * If the expandValues argument is null or the placeholder name is missing 
     * the placeholders will be searched in the {@link System} properties.
     * @param text the text
     * @param expandValues Properties
     * @return the text with the expandable placeholders expanded 
     */
    public static String expandProperties(String text, Properties expandValues) {
        StringBuilder ret = new StringBuilder();
        
        if (text != null) {
            StringTokenizer tok = new StringTokenizer(text,"}");
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                int propStartPos = token.indexOf("${");
                String propertyValue = "";
                if (propStartPos > -1) {
                    String propertyName = token.substring(propStartPos+2);
                    String trimPropertyName = propertyName.trim();
                    if (expandValues == null) {
                		propertyValue = System.getProperty(trimPropertyName);
                    } else {
                    	propertyValue = expandValues.containsKey(trimPropertyName)
                    		? expandValues.getProperty(trimPropertyName)
                    		: System.getProperty(trimPropertyName);
                    }
                    if (propertyValue == null) {
                        propertyValue = "${" + propertyName + "}";
                    } else {
                    	propertyValue = expandProperties(propertyValue, expandValues);
                    }
                    token = token.substring(0, propStartPos);
                } else {
                	token = token + (tok.hasMoreTokens() ? "}" : "");
                }
                ret.append(token);
                ret.append(propertyValue);
            }
        }
        return ret.toString();
    }

		public static Float parseFloat(String input, Float defaultValue) {
			Float ret = defaultValue;
			if (!Pedantic.isNullEmptyOrBlank(input)) {
				try {
					ret = Float.valueOf(input);
				} catch (NumberFormatException ignored) { }
			}
			return ret;
		}

		public static Double parseDouble(String input, Double defaultValue) {
			Double ret = defaultValue;
			if (!Pedantic.isNullEmptyOrBlank(input)) {
				try {
					ret = Double.valueOf(input);
				} catch (NumberFormatException ignored) { }
			}
			return ret;
		}

    public static String uriEncode(String value, char[] unencodedChars) {
        if (isNullOrEmpty(value)) {
            return value;
        }
        char[] input = value.toCharArray();
        StringBuilder ret = new StringBuilder();
        if (unencodedChars == null || unencodedChars.length == 0) {
	        for (char c : input) {
		        ret.append(percentEncode(c));
	        }
        } else {
            Arrays.sort(unencodedChars);
	        for (char ch : input) {
		        if (Arrays.binarySearch(unencodedChars, ch) > -1) {
			        ret.append(ch);
		        } else {
			        ret.append(percentEncode(ch));
		        }
	        }
        }
        return ret.toString();
    }

    public static String uriEncodeHeader(String value) {
        return uriEncode(value, (UNRESERVED_RFC3986 + "/ ").toCharArray());
    }
    public static String uriEncode(String value, boolean encodeSlash) {
        return uriEncode(value, (encodeSlash ? UNRESERVED_RFC3986 : UNRESERVED_RFC3986 + "/").toCharArray());
    }

	public static String getSafeFilename(String value) {
		char[] input = value.toCharArray();
		StringBuilder ret = new StringBuilder();
		for (char ch : input) {
			if (Arrays.binarySearch(FORBIDDEN_FILENAME_CHARACTERS, ch) > -1) {
				ret.append(percentEncode(ch));
			} else {
				ret.append(ch);
			}
		}
		return ret.toString();
	}
	
	
	public static String percentEncode(String value) {
        return percentEncode(value, null, false);
    }
	public static String percentEncode(String value, Charset charset) {
      return percentEncode(value, charset, false);
  }
	public static String percentEncode(String value, Charset charset, boolean lowerCase) {
		final char[] hexArray = lowerCase ? HEX_ARRAY_LOWER : HEX_ARRAY_UPPER;
		if (value == null) {
		  return "";
		}
		byte[] data = value.getBytes(charset == null ? CONFIG.getCharset() : charset);
		char[] hexChars = new char[data.length * 3];
		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			hexChars[j * 3] = '%';
			hexChars[j * 3 + 1] = hexArray[v >>> 4];
			hexChars[j * 3 + 2] = hexArray[v & 0x0F];
		}
		return String.valueOf(hexChars);
	}
	public static String percentEncode(char value) {
	return percentEncode(value, null, false);
	}
	public static String percentEncode(char value, Charset charset) {
	return percentEncode(value, charset, false);
	}
	public static String percentEncode(char value, Charset charset, boolean lowerCase) {
		return percentEncode(String.valueOf(value), charset, lowerCase);
	}

//  public static byte[] fromHex(String hexData) {
//	  byte[] result = new byte[(hexData.length() + 1) / 2];
//	  String hexNumber;
//	  int stringOffset = 0;
//	  int byteOffset = 0;
//	  while (stringOffset < hexData.length()) {
//		  hexNumber = hexData.substring(stringOffset, stringOffset + 2);
//		  stringOffset += 2;
//		  result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
//	  }
//	  return result;
//  }
	public static byte[] fromHex(String hexData) {
	    return new BigInteger(hexData, 16).toByteArray();
	}

	public static String hexEncode(byte[] data) {
		return hexEncode(data, false);
	}
	public static String hexEncode(byte[] data, boolean upperCase) {
		final char[] hexArray = upperCase ? HEX_ARRAY_UPPER : HEX_ARRAY_LOWER;
		if (data == null) {
			return "";
		}
		char[] hexChars = new char[data.length * 2];
		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return String.valueOf(hexChars);
	}

	public static String hexEncode2(byte[] array) {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if(paddingLength > 0)
			return String.format("%0" + paddingLength + "d", 0) + hex;
		else
			return hex;
	}
		
	/**
	 * Return the byteSize of String
	 */
	public static int byteSize(String s, Charset charset) {
		return getEncodedBytes(s, charset).length;
	}
}
