package liquer.alchemy.xmlcrypto.crypto;

import org.junit.jupiter.api.Test;

import java.security.Security;

/**
 * Basic class to confirm the Bouncy Castle provider is 
 * installed.
 */
@SuppressWarnings("unchecked")
public class SimpleProviderTest {

    static {
        Security.setProperty("crypto.policy", "unlimited");
    }

    @Test
    void testProvider() {
        String providerName = "BC";
        
        if (Security.getProvider(providerName) == null) {
            // System.out.println(providerName + " provider not installed");
        } else {
            // System.out.println(providerName + " is installed.");
        }
    }
}
