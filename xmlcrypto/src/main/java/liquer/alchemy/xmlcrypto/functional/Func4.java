package liquer.alchemy.xmlcrypto.functional;

/** (a0, a1, a2, a3) -&gt; b */
@FunctionalInterface public interface Func4<A0, A1, A2, A3, B> {
    default Func4<A0, A1, A2, A3, B> apply() { return this; }
    default Func3<A1, A2, A3, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2, A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
    }
    default Func2<A2, A3, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2, A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
    }
    default Func1<A3, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
        return (A3 arg3) -> this.apply(arg0, arg1, arg2, arg3);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
}