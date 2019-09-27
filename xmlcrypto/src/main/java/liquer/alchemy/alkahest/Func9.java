package liquer.alchemy.alkahest;

/** (a0, a1, a2, a3, a4, a5, a6, a7, a8) -&gt; b */
@FunctionalInterface public interface Func9<A0, A1, A2, A3, A4, A5, A6, A7, A8, B> {
    default  Func9<A0, A1, A2, A3, A4, A5, A6, A7, A8, B> apply() { return this; }
    default  Func8<A1, A2, A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func7<A2, A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func6<A3, A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
        return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func5<A4, A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
        return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func4<A5, A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
        return (A5 arg5, A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func3<A6, A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
        return (A6 arg6, A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func2<A7, A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
        return (A7 arg7, A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    default  Func1<A8, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
        return (A8 arg8) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8);
}