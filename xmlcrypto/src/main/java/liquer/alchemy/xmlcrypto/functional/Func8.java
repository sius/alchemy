package liquer.alchemy.xmlcrypto.functional;

/** (a0, a1, a2, a3, a4, a5, a6, a7) -&gt; b */
@FunctionalInterface public interface Func8<A0, A1, A2, A3, A4, A5, A6, A7, B> {
    default  Func8<A0, A1, A2, A3, A4, A5, A6, A7, B> apply() { return this; }
    default  Func7<A1, A2, A3, A4, A5, A6, A7, B> apply(final A0 a0) {
        return (A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func6<A2, A3, A4, A5, A6, A7, B> apply(final A0 a0, final A1 a1) {
        return (A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func5<A3, A4, A5, A6, A7, B> apply(final A0 a0, final A1 a1, final A2 a2) {
        return (A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func4<A4, A5, A6, A7, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3) {
        return (A4 a4, A5 a5, A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func3<A5, A6, A7, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3, final A4 a4) {
        return (A5 a5, A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func2<A6, A7, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5) {
        return (A6 a6, A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    default  Func1<A7, B> apply(final A0 a0, final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6) {
        return (A7 a7) -> this.apply(a0, a1, a2, a3, a4, a5, a6, a7);
    }
    B apply(A0 a0, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7);
}