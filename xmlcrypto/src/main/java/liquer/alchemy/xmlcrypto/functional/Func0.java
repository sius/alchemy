package liquer.alchemy.xmlcrypto.functional;

import java.util.function.Supplier;

/** () -&gt; b */
@FunctionalInterface public interface Func0<B> extends Supplier<B> {
    default B apply() { return this.get(); }
    B get();
}