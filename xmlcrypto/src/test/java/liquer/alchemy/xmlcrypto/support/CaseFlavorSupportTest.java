package liquer.alchemy.xmlcrypto.support;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class CaseFlavorSupportTest {

    @Test
    public void testToTitleCase() {
        assertEquals("Test", CaseFlavorSupport.toTitleCase("test"));
        assertEquals("Test test", CaseFlavorSupport.toTitleCase("test test"));
        assertEquals("1test", CaseFlavorSupport.toTitleCase("1test"));

        assertEquals("", CaseFlavorSupport.toTitleCase(null));
        assertEquals("", CaseFlavorSupport.toTitleCase("   "));
    }

    @Test
    public void testToSeparatedTitleCase() {
        assertEquals("Test", CaseFlavorSupport.toSeparatedTitleCase("test", ' '));
        assertEquals("Test Test", CaseFlavorSupport.toSeparatedTitleCase("testTest", ' '));
        assertEquals("1 Test", CaseFlavorSupport.toSeparatedTitleCase("1Test", ' '));

        assertEquals("", CaseFlavorSupport.toSeparatedTitleCase(null, ' '));
    }

    @Test
    public void testToSeparatedLowerCase() {
        assertEquals("test", CaseFlavorSupport.toSeparatedLowerCase("test", ' ', Locale.getDefault()));
        assertEquals("test test", CaseFlavorSupport.toSeparatedLowerCase("testTest", ' ', Locale.getDefault()));
        assertEquals("1 test", CaseFlavorSupport.toSeparatedLowerCase("1Test", ' ', Locale.getDefault()));

        assertEquals("", CaseFlavorSupport.toSeparatedLowerCase(null, ' ', Locale.getDefault()));
    }

    @Test
    public void testEnsureLocale() {
        Assert.assertEquals(Locale.US, CaseFlavorSupport.ensure(null));
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
    public void testToLispCaseWithDefaultLocale() {
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toLispCase("AaBbCcDdEeXxA1SSß", Locale.getDefault()));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street", Locale.getDefault()));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toLispCase("aa-bb-cc-dd-ee-xx-a1-ssß", Locale.getDefault()));
    }

    @Test
    public void testToKebabCase() {
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("AaBbCcDdEeXxA1SSß"));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street"));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa-bb-cc-dd-ee-xx-a1-ssß"));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa_bb_cc_dd_ee_xx_a1_ssß"));
    }
    @Test
    public void testToKebabCaseWithLocale() {
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("AaBbCcDdEeXxA1SSß", Locale.getDefault()));
        assertEquals("street", CaseFlavorSupport.toKebabCase("street", Locale.getDefault()));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa-bb-cc-dd-ee-xx-a1-ssß", Locale.getDefault()));
        assertEquals("aa-bb-cc-dd-ee-xx-a1-ssß", CaseFlavorSupport.toKebabCase("aa_bb_cc_dd_ee_xx_a1_ssß", Locale.getDefault()));
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
    public void testToSnakeCaseWithDefaultLLocale() {
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("aaBbCcDdEeXxA1SSß", Locale.getDefault()));
        assertEquals("street", CaseFlavorSupport.toSnakeCase("street", Locale.getDefault()));
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("aa_bb_cc_dd_ee_xx_a1_ssß", Locale.getDefault()));
        assertEquals("aa_bb_cc_dd_ee_xx_a1_ssß", CaseFlavorSupport.toSnakeCase("Aa-Bb-Cc-Dd-Ee-Xx-A1-SSß", Locale.getDefault()));
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
        assertEquals(ident, CaseFlavorSupport.identity(ident));
    }
}
