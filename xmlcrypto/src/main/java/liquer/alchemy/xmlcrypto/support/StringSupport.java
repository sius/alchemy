package liquer.alchemy.xmlcrypto.support;

import java.lang.reflect.Array;

public final class StringSupport {

	private static final String EMPTY = "";

	private StringSupport() { }
	
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
        return (value == null || value.isEmpty());
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
        return (value == null || value.trim().isEmpty());
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


    public static String stringify(Object value) {
        return stringify(value, "");
    }

    public static String stringify(Object value, String defaultValue) {
        String ret = defaultValue;
        if (value == null) {
            return ret;
        }
        ret = EMPTY;
        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            if (len > 0) {
                Object v = Array.get(value, 0);
                ret = (v != null ? v.toString() : "");
            }
        } else {
            ret = value.toString();
        }
    	return ret;
    }
}
