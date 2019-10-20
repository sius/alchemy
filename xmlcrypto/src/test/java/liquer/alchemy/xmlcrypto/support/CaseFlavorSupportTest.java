package liquer.alchemy.xmlcrypto.support;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaseFlavorSupportTest {

    @Test
    public void testJoinUrl() {
        String expected = "http://localhost:9080/v1/customers/1/image";
        String actual = CaseFlavorSupport.joinUrl("http://localhost:9080/v1/", "/customers/", "1/", "/image");
        assertEquals(expected, actual);
    }

    @Test
    public void testToCamelCase() {
        assertEquals("aaBbCcDdEeFfGgXxA1SSß", CaseFlavorSupport.toCamelCase("Aa bb.cc_dd-ee:ff,gg;xx a1 ßß"));
        assertEquals("street", CaseFlavorSupport.toCamelCase("street"));
        assertEquals("aaBbCcDdEeFfGgXxA1SSß", CaseFlavorSupport.toCamelCase("aaBbCcDdEeFfGgXxA1SSß"));
    }

    @Test
    public void testToPascalCase() {
        assertEquals("AaBbCcDdEeFfGgXxA1SSß", CaseFlavorSupport.toPascalCase("Aa bb.cc_dd-ee:ff,gg;xx a1 ßß"));
        assertEquals("Street", CaseFlavorSupport.toPascalCase("street"));
        assertEquals("AaBbCcDdEeFfGgXxA1SSß", CaseFlavorSupport.toPascalCase("AaBbCcDdEeFfGgXxA1SSß"));

    }

    @Test
    public void testToLispCase() {
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toLispCase("AaBbCcDdEeXxA1SSß"));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street"));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toLispCase("aa-bb-cc-dd-ee-xx-a1-ssß"));
    }

    @Test
    public void testToKebabCase() {
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("AaBbCcDdEeXxA1SSß"));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street"));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa-bb-cc-dd-ee-xx-a1-ssß"));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa_bb_cc_dd_ee_xx_a1_ssß"));
    }

    @Test
    public void testToTrainCase() {
        assertEquals("Aa-Bb-Cc-Dd-Ee-Xx-A1-SSß", CaseFlavorSupport.toTrainCase("aaBbCcDdEeXxA1SSß"));
        assertEquals("Street", CaseFlavorSupport.toTrainCase("street"));
        assertEquals("Aa-Bb-Cc-Dd-Ee-Xx-A1-SSß", CaseFlavorSupport.toTrainCase("Aa-Bb-Cc-Dd-Ee-Xx-A1-SSß"));
        assertEquals("Aa-Bb-Cc-Dd-Ee-Xx-A1-Ssß", CaseFlavorSupport.toTrainCase("aa_bb_cc_dd_ee_xx_a1_ssß"));
        assertEquals("A-B-C-D-E-X-A-S-ß", CaseFlavorSupport.toTrainCase("a_b_c_d_e_x_a_s_ß"));
    }

    @Test
    public void testToSnakeCase() {
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("aaBbCcDdEeXxA1SSß"));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street"));
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("aa_bb_cc_dd_ee_xx_a1_ssß"));
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("Aa-Bb-Cc-Dd-Ee-Xx-A1-SSß"));
    }

    @Test
    public void testToCSharpProperty() {
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customerId"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer_Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer-Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer:Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer,Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer;Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer|Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer/Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer Id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer   --  Id"));

        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer_id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer-id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer:id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer,id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer;id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer|id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer/id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer id"));
        assertEquals("CustomerId", CaseFlavorSupport.toPascalCase("customer   --  id"));

        assertEquals("Street", CaseFlavorSupport.toPascalCase("street"));
        assertEquals("Street", CaseFlavorSupport.toPascalCase("Street"));

    }

    @Test
    public void testToPrefixedPascalCase() {
        String prefix = "set";
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customerId"));
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customer_Id"));
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customer-Id"));
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customer:Id"));
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customer Id"));
        assertEquals("setCustomerId", CaseFlavorSupport.toPrefixedPascalCase(prefix, "customer   --  Id"));
        assertEquals("setStreet", CaseFlavorSupport.toPrefixedPascalCase(prefix, "street"));
        assertEquals("setStreet", CaseFlavorSupport.toPrefixedPascalCase(prefix, "Street"));
    }

    @Test
    public void testToJavaGetter() {
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customerId"));
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customer_Id"));
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customer-Id"));
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customer:Id"));
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customer Id"));
        assertEquals("getCustomerId", CaseFlavorSupport.toJavaGetter("customer   --  Id"));
        assertEquals("getStreet", CaseFlavorSupport.toJavaGetter("street"));
        assertEquals("getStreet", CaseFlavorSupport.toJavaGetter("Street"));
    }

    @Test
    public void testIdentity() {
        String ident = "aooäfg   7(()okasägfkoaäsefgkeä#sgksb#d";
        assertEquals(ident, StringSupport.identity(ident));
    }
}
