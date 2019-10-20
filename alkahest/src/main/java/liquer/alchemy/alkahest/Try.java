package liquer.alchemy.alkahest;

import java.util.function.Supplier;

/**
 * Exception Guard:
 *
 * Exception handling support for special cases,
 * for example:
 *      inside a FunctionalInterface
 *   or
 *      if you don't prefer to write catch blocks
 *
 * It's main task is to wrap a Throwable with a RuntimeException
 * (TryableException)
 * Try provides a similar programming model compared to Optional,
 * but for Throwable instead of null values.
 *
 * Usage:
 *      B result = Try.of(() -> ...).orElseThrow()
 *      B result = Try.of(() -> ...).orElseGet(defaultValue)
 *      B result = Try.of(() -> ...).orElseThrow(() -> new ...Exception)
 *
 * @param <B> the internal value to retrieve with get();
 */
public final class Try<B> {

    /**
     * Create the Exception guard
     *
     * @param t0 the Tryable
     * @param <B> the supplied internal result value type
     * @return
     */
    public static <B> Try<B> of(Tryable<B> t0) {
        try {
            return new Try(t0.get(), null);
        } catch (Throwable t) {
            return new Try(null, t);
        }
    }

    private final B value;
    private final Throwable throwable;
    private Try(B value, Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }

    /**
     *
     * @return true if no exception has been thrown
     */
    public boolean isSuccess() { return throwable == null; }

    /**
     *
     * @return the opposite of isSuccess()
     */
    public boolean isFailure() { return !isSuccess(); }

    /**
     * In case of a failure it will throw the original exception
     * wrapped inside an RuntimeException.
     *
     * @return the value
     * @throws TryableException
     */
    public B orElseThrow() throws TryableException {
        return orElseThrow(() -> throwable);
    }

    /**
     * In case of a failure it will throw the provided exception
     * wrapped inside a RuntimeException.
     *
     * @param t
     * @return the value
     * @throws TryableException
     */
    public B orElseThrow(Supplier<Throwable> t) throws TryableException {
        if (isFailure()) {
            throw new TryableException(t.get());
        }
        return value;
    }

    /**
     * The provided value will be returned
     * when there was an exception
     * while executing the Tryable
     *
     * @param value
     * @return the internal or provided value
     */
    public B orElseGet(B value) {
        return isFailure() ? value : this.value;
    }
}
