package liquer.alchemy.xmlcrypto.functional;

/** (a0, a1, a2, a3, a4, a5, a6) -&gt; b */
@FunctionalInterface public interface Func7<A0, A1, A2, A3, A4, A5, A6, B> {

    default  Func7<A0, A1, A2, A3, A4, A5, A6, B> apply() { return this; }
    default  Func6<A1, A2, A3, A4, A5, A6, B> apply(final A0 a0) {
        return (A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    default  Func5<A2, A3, A4, A5, A6, B> apply(final A0 a0, final A1 a1) {
        return (A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    default  Func4<A3, A4, A5, A6, B> apply(final A0 a0, final A1 a1, final A2 a2) {
        return (A3 a3, A4 a4, A5 a5, A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    default  Func3<A4, A5, A6, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3) {
        return (A4 a4, A5 a5, A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    default  Func2<A5, A6, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3, final A4 a4) {
        return (A5 a5, A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    default  Func1<A6, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5) {
        return (A6 a6) -> this.apply(a0, a1, a2, a3, a4, a5, a6);
    }
    B apply(A0 a0, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6);
}