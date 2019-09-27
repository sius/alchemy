package liquer.alchemy.alkahest;

import java.util.function.Supplier;

/** () -&gt; b */
@FunctionalInterface public interface Func0<B> extends Supplier<B> {
    default B apply() { return this.get(); }
    B get();
}