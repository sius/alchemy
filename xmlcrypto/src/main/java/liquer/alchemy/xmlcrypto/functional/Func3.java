package liquer.alchemy.xmlcrypto.functional;

/** (a0, a1, a2) -&gt; b */
@FunctionalInterface public interface Func3<A0, A1, A2, B> {
    default Func3<A0, A1, A2, B> apply() { return this; }
    default Func2<A1, A2, B> apply(final A0 arg0) {
        return (A1 arg1, A2 arg2) -> this.apply(arg0, arg1, arg2);
    }
    default Func1<A2, B> apply(final A0 arg0, final A1 arg1) {
        return (A2 arg2) ->  this.apply(arg0, arg1, arg2);
    }
    B apply(A0 arg0, A1 arg1, A2 arg2);
}