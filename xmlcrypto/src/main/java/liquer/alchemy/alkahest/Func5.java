package liquer.alchemy.alkahest;

/** (a0, a1, a2, a3, a4) -&gt; b */
@FunctionalInterface public interface Func5<A0, A1, A2, A3, A4, B> {
    default  Func5<A0, A1, A2, A3, A4, B> apply() { return this; }
    default  Func4<A1, A2, A3, A4, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2, A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
    }
    default  Func3<A2, A3, A4, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2, A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
    }
    default  Func2<A3, A4, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
        return (A3 arg3, A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
    }
    default  Func1<A4, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
        return (A4 arg4) -> this.apply(arg0, arg1, arg2, arg3, arg4);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
}