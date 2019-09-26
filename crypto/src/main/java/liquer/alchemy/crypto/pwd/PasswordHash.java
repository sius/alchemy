package liquer.alchemy.crypto.pwd;

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * PBKDF2 salted password hashing.
 */
public class PasswordHash
{
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // The following constants may be changed without breaking existing hashes.
    public static final int SALT_BYTES 			= 24;
    public static final int HASH_BYTES 			= 24;
    public static final int PBKDF2_ITERATIONS 	= 1000;

    public static final int ITERATION_INDEX 	= 0;
    public static final int SALT_INDEX 			= 1;
    public static final int PBKDF2_INDEX 		= 2;

    /**
     * Returns a salted PBKDF2 hash of the password.
     * @param password - the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public static String createHash(String password) {
        return createHash(password.toCharArray());
    }
    /**
     * Returns a salted PBKDF2 hash of the password.
     * @param password - the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public static String createHash(char[] password) {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTES);
        // format iterations:salt:hash
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);
    }
    public static String createToken(String value, byte[] salt, int iterations) {
    	return createToken(value.toCharArray(), salt, iterations);
    }
    public static String createToken(char[] value, byte[] salt, int iterations) {
        // Hash the password
        byte[] hash = pbkdf2(value, salt, iterations, HASH_BYTES);
        // format iterations:hash
        return toHex(hash);
    }
    public static boolean validateToken(String value, String goodHash, byte[] salt, int iterations) {
    	return validateToken(value.toCharArray(), goodHash, salt, iterations);
    }
    public static boolean validateToken(char[] password, String goodHash, byte[] salt, int iterations) {
        byte[] hash = fromHex(goodHash);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }
    public static String createSecureRandom(int saltBytes) {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltBytes];
        random.nextBytes(salt);
        return toHex(salt);
    }
    /**
     * Validates a password using a hash.
     * @param password - the password to check
     * @param goodHash - the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String goodHash) {
        return validatePassword(password.toCharArray(), goodHash);
    }
    /**
     * Validates a password using a hash.
     * @param password - the password to check
     * @param goodHash - the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(char[] password, String goodHash) {
        // Decode the hash into its parameters
        String[] params = goodHash.split(":");
        int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] hash = fromHex(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     * @param a - the first byte array
     * @param b - the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }
    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param password - the password to hash.
     * @param salt - the salt byte array
     * @param iterations - the iteration count (slowness factor)
     * @param bytes - the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
    	return CPbkdf2.nativeDerive(password, salt, iterations, bytes);
//    	PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
//        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
//        return skf.generateSecret(spec).getEncoded();
    }
    /**
     * Converts a string of hexadecimal characters into a byte array.
     * @param hex - the hex string
     * @return the hex string decoded into a byte array
     */
    public static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        return binary;
    }
    /**
     * Converts a byte array into a hexadecimal string.
     * @param array - the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    public static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }
    /**
     * Tests the basic functionality of the PasswordHash class
     * @param args ignored
     */
    public static void main(String[] args) {
        try {
            // Print out 10 hashes
            for(int i = 0; i < 10; i++)
                System.out.println(PasswordHash.createHash("p\r\nassw0Rd!"));

            // Test password validation
            boolean failure = false;
            System.out.println("Running tests...");
            for(int i = 0; i < 100; i++) {
                String password = ""+i;
                String hash = createHash(password);
                String secondHash = createHash(password);
                if(hash.equals(secondHash)) {
                    System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
                    failure = true;
                }
                String wrongPassword = ""+(i+1);
                if(validatePassword(wrongPassword, hash)) {
                    System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
                    failure = true;
                }
                if(!validatePassword(password, hash)) {
                    System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
                    failure = true;
                }
            }
            if(failure)
                System.out.println("TESTS FAILED!");
            else
                System.out.println("TESTS PASSED!");
        }
        catch(Exception ex) {
            System.out.println("ERROR: " + ex);
        }
    }
}
