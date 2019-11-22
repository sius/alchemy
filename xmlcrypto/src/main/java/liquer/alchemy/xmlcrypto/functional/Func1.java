package liquer.alchemy.xmlcrypto.functional;

import java.util.function.Function;

/** (arg0) -&gt; b */
@FunctionalInterface public interface Func1<A0, B> extends Function<A0, B> {
    default Func1<A0, B> andThen(Func1<B, B> next) {
        return (A0 arg0) -> next.apply(this.apply(arg0));
    }
    default Func1<A0, B> compose(Func1<A0, A0> next) {
        return (A0 arg0) -> this.apply(next.apply(arg0));
    }
    default Func1<A0, B> apply() { return this; }
    B apply(A0 arg0);
}