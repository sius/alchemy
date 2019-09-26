package liquer.alchemy.crypto.alg;

public interface Algorithm {

    /**
     *
     * @return The Algorithm Name (Java Context)
     */
    String getName();

    /**
     *
     * @return The Algorithm URI
     */
    String getIdentifier();
}
