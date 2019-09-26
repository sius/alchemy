package liquer.alchemy.crypto.xml.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterEncoder {

    private static class SingletonHelper {
        private static final Map<String, String> xml_special_to_encoded_attribute;
        private static final Map<String, String> xml_special_to_encoded_text;
        private static final CharacterEncoder INSTANCE;

        static {
            xml_special_to_encoded_attribute = new HashMap<>();
            xml_special_to_encoded_attribute.put("&", "&amp;");
            xml_special_to_encoded_attribute.put("<", "&lt;");
            xml_special_to_encoded_attribute.put("\"", "&quot;");
            xml_special_to_encoded_attribute.put("\r", "&#xD;");
            xml_special_to_encoded_attribute.put("\n", "&#xA;");
            xml_special_to_encoded_attribute.put("\t", "&#x9;");

            xml_special_to_encoded_text = new HashMap<>();
            xml_special_to_encoded_text.put("&", "&amp;");
            xml_special_to_encoded_text.put("<", "&lt;");
            xml_special_to_encoded_text.put(">", "&gt;");
            xml_special_to_encoded_text.put("\r", "&#xD;");

            INSTANCE = new CharacterEncoder(xml_special_to_encoded_attribute, xml_special_to_encoded_text);
        }
    }

    public static CharacterEncoder getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private Map<String, String> attributeCharacterMap;
    private Map<String, String> textCharacterMap;

    private CharacterEncoder(
        Map<String, String> attributeCharacterMap,
        Map<String, String> textCharacterMap) {
        this.attributeCharacterMap = attributeCharacterMap;
        this.textCharacterMap = textCharacterMap;
    }

    /**
     * Special character normalization. See:
     * <li>https://www.w3.org/TR/xml-c14n#ProcessingModel (Attribute Nodes)
     * <li>https://www.w3.org/TR/xml-c14n#Example-Chars
     * @param attributeValue The attribute value
     * @return encoded value
     */
    public String encodeAttributeValue(String attributeValue) { return normalizeAttribute(normalizeWhiteSpace(attributeValue)); }
    public String encodeText(String text) {
        return normalizeText(normalizeLineEnding(text));
    }

    private String normalizeWhiteSpace(String value) {
        return value.replaceAll("[\r\n\t]+", " ");
    }
    private String normalizeAttribute(String attributeValue) {
        return normalize(Pattern.compile("([&<\"\r\n\t])"), this.attributeCharacterMap, attributeValue);
    }

    /*
     * this should normally be done by the xml parser
     * See: https://www.w3.org/TR/xml/#sec-line-ends
     */
    private String normalizeLineEnding(String value) {
        return value.replaceAll("\r\n?]+", "\n");
    }

    /*
     * Special character normalization in Text Nodes. See:
     * <li>https://www.w3.org/TR/xml-c14n#ProcessingModel (Text Nodes)
     * <li>https://www.w3.org/TR/xml-c14n#Example-Chars
     */
    private String normalizeText(String text) {
        return normalize(Pattern.compile("([&<>\r])"), this.textCharacterMap, text);
    }

    private String normalize(Pattern p, Map<String, String> map, String value) {
        String ret = value;
        Matcher m = p.matcher(value);
        while(m.find()) {
            ret = m.replaceAll(map.getOrDefault(m.group(1), m.group(1)));
        }
        return ret;
    }
}
