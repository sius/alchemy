package liquer.alchemy.xmlcrypto.functional;

@FunctionalInterface public interface Tryable<B> {
    B get() throws Throwable;
}
