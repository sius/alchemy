package liquer.alchemy.alkahest;

@FunctionalInterface public interface Tryable<B> {
    B get() throws Throwable;
}
