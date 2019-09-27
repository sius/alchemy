package liquer.alchemy.alkahest;

/** (a0, a1, a2, a3, a4, a5) -&gt; b */
@FunctionalInterface public interface Func6<A0, A1, A2, A3, A4, A5, B> {
    default  Func6<A0, A1, A2, A3, A4, A5, B> apply() { return this; }
    default  Func5<A1, A2, A3, A4, A5, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    default  Func4<A2, A3, A4, A5, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2, A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    default  Func3<A3, A4, A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
        return (A3 arg3, A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    default  Func2<A4, A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
        return (A4 arg4, A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    default  Func1<A5, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
        return (A5 arg5) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
}