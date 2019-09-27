package liquer.alchemy.alkahest;

import java.util.function.BiFunction;

/** (a0, a1) -&gt; b */
@FunctionalInterface public interface Func2<A0, A1, B> extends BiFunction<A0, A1, B> {
    default Func2<A0, A1, B> apply() { return this; }
    default Func1<A1, B> apply(final A0 arg0) {
        return (arg1) -> this.apply(arg0, arg1);
    }
    B apply(A0 arg0, A1 arg1);
}