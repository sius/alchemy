package liquer.alchemy.alkahest;

/** (a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12) -&gt; b */
@FunctionalInterface public interface Func13<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> {
    default  Func13<A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply() { return this; }
    default  Func12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func11<A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func10<A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2) {
        return (A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func9<A4, A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3) {
        return (A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func8<A5, A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
        return (A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func7<A6, A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
        return (A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func6<A7, A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
        return (A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func5<A8, A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7) {
        return (A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func4<A9, A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8) {
        return (A9 arg9, A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func3<A10, A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9) {
        return (A10 arg10, A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func2<A11, A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10) {
        return (A11 arg11, A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    default  Func1<A12, B> apply(final A0 arg0, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6, final A7 arg7, final A8 arg8, final A9 arg9, final A10 arg10, final A11 arg11) {
        return (A12 arg12) -> this.apply(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9, A10 arg10, A11 arg11, A12 arg12);
}
