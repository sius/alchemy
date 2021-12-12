package liquer.alchemy.xmlcrypto.functional;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuncTest {
    @Test
    void testCurryingF9() {
        Func9<String, Boolean, Short, Integer, Float, Double, Long, String, String, String> concat =
            (a1, a2, a3, a4, a5, a6, a7, a8, a9) -> a1 + a2 + a3 + a4 + a5 + a6 +a7 + a8 + a9;

        final String expected = "atrue123.04.0567";

        Func9<String, Boolean, Short, Integer, Float, Double, Long, String, String, String> partial9 = concat.apply();
        Func8<Boolean, Short, Integer, Float, Double, Long, String, String, String> partial8 = concat.apply("a");
        Func7<Short, Integer, Float, Double, Long, String, String, String> partial7 = concat.apply("a", true);
        Func6<Integer, Float, Double, Long, String, String, String> partial6 = concat.apply("a", true, (short)1);
        Func5<Float, Double, Long, String, String, String> partial5 = concat.apply("a", true, (short)1, 2);
        Func4<Double, Long, String, String, String> partial4 = concat.apply("a", true, (short)1, 2, 3.0f);
        Func3<Long, String, String, String> partial3 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0);
        Func2<String, String, String> partial2 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L);
        Func1<String, String> partial1 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L, "6");
        String actual = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L, "6", "7");

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply("7"));
        assertEquals(expected, partial2.apply("6", "7"));
        assertEquals(expected, partial3.apply(5L, "6", "7"));
        assertEquals(expected, partial4.apply(4.0, 5L, "6", "7"));
        assertEquals(expected, partial5.apply(3.0f, 4.0, 5L, "6", "7"));
        assertEquals(expected, partial6.apply(2, 3.0f, 4.0, 5L, "6", "7"));
        assertEquals(expected, partial7.apply((short)1, 2, 3.0f, 4.0, 5L, "6", "7"));
        assertEquals(expected, partial8.apply(true, (short)1, 2, 3.0f, 4.0, 5L, "6", "7"));
        assertEquals(expected, partial9.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L, "6", "7"));
    }

    @Test
    void testCurryingF8() {
        Func8<String, Boolean, Short, Integer, Float, Double, Long, String, String> concat =
            (a1, a2, a3, a4, a5, a6, a7, a8) -> a1 + a2 + a3 + a4 + a5 + a6 +a7 + a8;

        final String expected = "atrue123.04.056";

        Func8<String, Boolean, Short, Integer, Float, Double, Long, String, String> partial8 = concat.apply();
        Func7<Boolean, Short, Integer, Float, Double, Long, String, String> partial7 = concat.apply("a");
        Func6<Short, Integer, Float, Double, Long, String, String> partial6 = concat.apply("a", true);
        Func5<Integer, Float, Double, Long, String, String> partial5 = concat.apply("a", true, (short)1);
        Func4<Float, Double, Long, String, String> partial4 = concat.apply("a", true, (short)1, 2);
        Func3<Double, Long, String, String> partial3 = concat.apply("a", true, (short)1, 2, 3.0f);
        Func2<Long, String, String> partial2 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0);
        Func1<String, String> partial1 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L);
        String actual = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L, "6");

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply("6"));
        assertEquals(expected, partial2.apply(5L, "6"));
        assertEquals(expected, partial3.apply(4.0, 5L, "6"));
        assertEquals(expected, partial4.apply(3.0f, 4.0, 5L, "6"));
        assertEquals(expected, partial5.apply(2, 3.0f, 4.0, 5L, "6"));
        assertEquals(expected, partial6.apply((short)1, 2, 3.0f, 4.0, 5L, "6"));
        assertEquals(expected, partial7.apply(true, (short)1, 2, 3.0f, 4.0, 5L, "6"));
        assertEquals(expected, partial8.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L, "6"));
    }

    @Test
    void testCurryingF7() {
        Func7<String, Boolean, Short, Integer, Float, Double, Long, String> concat =
            (a1, a2, a3, a4, a5, a6, a7) -> a1 + a2 + a3 + a4 + a5 + a6 +a7;

        final String expected = "atrue123.04.05";

        Func7<String, Boolean, Short, Integer, Float, Double, Long, String> partial7 = concat.apply();
        Func6<Boolean, Short, Integer, Float, Double, Long, String> partial6 = concat.apply("a");
        Func5<Short, Integer, Float, Double, Long, String> partial5 = concat.apply("a", true);
        Func4<Integer, Float, Double, Long, String> partial4 = concat.apply("a", true, (short)1);
        Func3<Float, Double, Long, String> partial3 = concat.apply("a", true, (short)1, 2);
        Func2<Double, Long, String> partial2 = concat.apply("a", true, (short)1, 2, 3.0f);
        Func1<Long, String> partial1 = concat.apply("a", true, (short)1, 2, 3.0f, 4.0);
        String actual = concat.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply(5L));
        assertEquals(expected, partial2.apply(4.0, 5L));
        assertEquals(expected, partial3.apply(3.0f, 4.0, 5L));
        assertEquals(expected, partial4.apply(2, 3.0f, 4.0, 5L));
        assertEquals(expected, partial5.apply((short)1, 2, 3.0f, 4.0, 5L));
        assertEquals(expected, partial6.apply(true, (short)1, 2, 3.0f, 4.0, 5L));
        assertEquals(expected, partial7.apply("a", true, (short)1, 2, 3.0f, 4.0, 5L));
    }

    @Test
    void testCurryingF6() {
        Func6<String, Boolean, Short, Integer, Float, Double, String> concat =
            (a1, a2, a3, a4, a5, a6) -> a1 + a2 + a3 + a4 + a5 + a6;

        final String expected = "atrue123.04.0";

        Func6<String, Boolean, Short, Integer, Float, Double, String> partial6 = concat.apply();
        Func5<Boolean, Short, Integer, Float, Double, String> partial5 = concat.apply("a");
        Func4<Short, Integer, Float, Double, String> partial4 = concat.apply("a", true);
        Func3<Integer, Float, Double, String> partial3 = concat.apply("a", true, (short)1);
        Func2<Float, Double, String> partial2 = concat.apply("a", true, (short)1, 2);
        Func1<Double, String> partial1 = concat.apply("a", true, (short)1, 2, 3.0f);
        String actual = concat.apply("a", true, (short)1, 2, 3.0f, 4.0);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply(4.0));
        assertEquals(expected, partial2.apply(3.0f, 4.0));
        assertEquals(expected, partial3.apply(2, 3.0f, 4.0));
        assertEquals(expected, partial4.apply((short)1, 2, 3.0f, 4.0));
        assertEquals(expected, partial5.apply(true, (short)1, 2, 3.0f, 4.0));
        assertEquals(expected, partial6.apply("a", true, (short)1, 2, 3.0f, 4.0));
    }

    @Test
    void testCurryingF5() {
        Func5<String, Boolean, Short, Integer, Float, String> concat =
            (a1, a2, a3, a4, a5) -> a1 + a2 + a3 + a4 + a5;

        final String expected = "atrue123.0";

        Func5<String, Boolean, Short, Integer, Float, String> partial5 = concat.apply();
        Func4<Boolean, Short, Integer, Float, String> partial4 = concat.apply("a");
        Func3<Short, Integer, Float, String> partial3 = concat.apply("a", true);
        Func2<Integer, Float, String> partial2 = concat.apply("a", true, (short)1);
        Func1<Float, String> partial1 = concat.apply("a", true, (short)1, 2);
        String actual = concat.apply("a", true, (short)1, 2, 3.0f);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply(3.0f));
        assertEquals(expected, partial2.apply(2, 3.0f));
        assertEquals(expected, partial3.apply((short)1, 2, 3.0f));
        assertEquals(expected, partial4.apply(true, (short)1, 2, 3.0f));
        assertEquals(expected, partial5.apply("a", true, (short)1, 2, 3.0f));
    }

    @Test
    void testCurryingF4() {
        Func4<String, Boolean, Short, Integer, String> concat =
            (a1, a2, a3, a4) -> a1 + a2 + a3 + a4;

        final String expected = "atrue12";

        Func4<String, Boolean, Short, Integer, String> partial4 = concat.apply();
        Func3<Boolean, Short, Integer, String> partial3 = concat.apply("a");
        Func2<Short, Integer, String> partial2 = concat.apply("a", true);
        Func1<Integer, String> partial1 = concat.apply("a", true, (short)1);
        String actual = concat.apply("a", true, (short)1, 2);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply(2));
        assertEquals(expected, partial2.apply((short)1, 2));
        assertEquals(expected, partial3.apply(true, (short)1, 2));
        assertEquals(expected, partial4.apply("a", true, (short)1, 2));
    }

    @Test
    void testCurryingF3() {
        Func3<String, Boolean, Short, String> concat =
            (a1, a2, a3) -> a1 + a2 + a3;

        final String expected = "atrue1";

        Func3<String, Boolean, Short, String> partial3 = concat.apply();
        Func2<Boolean, Short, String> partial2 = concat.apply("a");
        Func1<Short, String> partial1 = concat.apply("a", true);
        String actual = concat.apply("a", true, (short)1);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply((short)1));
        assertEquals(expected, partial2.apply(true, (short)1));
        assertEquals(expected, partial3.apply("a", true, (short)1));
    }

    @Test
    void testCurryingF2() {
        Func2<String, Boolean, String> concat =
            (a1, a2) -> a1 + a2;

        final String expected = "atrue";

        Func2<String, Boolean, String> partial2 = concat.apply();
        Func1<Boolean, String> partial1 = concat.apply("a");
        String actual = concat.apply("a", true);

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply(true));
        assertEquals(expected, partial2.apply("a", true));
    }

    @Test
    void testCurryingF1() {
        Func1<String, String> concat =
            (a1) -> a1;

        final String expected = "a";

        Func1<String, String> partial1 = concat.apply();
        String actual = concat.apply("a");

        assertEquals(expected, actual);
        assertEquals(expected, partial1.apply("a"));

        Func1<String, String> andThenF = partial1.andThen((s) -> s + "b");
        String x = andThenF.apply("a");
    }

    @Test
    void testCurryingF0() {
        Func0<String> concat = () -> "a";

        final String expected = "a";

        String actual = concat.apply();

        assertEquals(expected, actual);
    }
}
