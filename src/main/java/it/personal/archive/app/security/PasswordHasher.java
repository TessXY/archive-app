package it.personal.archive.app.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

public final class PasswordHasher {

    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;          // 128 bit
    private static final int KEY_LENGTH = 256;         // 256 bit di output
    private static final int DEFAULT_ITERATIONS = 210_000; // alza se la tua macchina regge

    private static final SecureRandom RNG = new SecureRandom();

    private PasswordHasher() {}

    /** Crea un hash sicuro nel formato:
     *  pbkdf2_sha256$<iterations>$<saltBase64>$<hashBase64>
     */
    public static String hash(String plainPassword) {
        return hash(plainPassword, DEFAULT_ITERATIONS);
    }

    public static String hash(String plainPassword, int iterations) {
        Objects.requireNonNull(plainPassword, "password nulla");
        byte[] salt = randomBytes(SALT_BYTES);
        byte[] dk = pbkdf2(plainPassword.toCharArray(), salt, iterations, KEY_LENGTH);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(dk);
        return String.format("pbkdf2_sha256$%d$%s$%s", iterations, saltB64, hashB64);
    }

    /** Verifica una password in chiaro contro l'hash salvato. */
    public static boolean verify(String plainPassword, String stored) {
        try {
            Objects.requireNonNull(plainPassword, "password nulla");
            Objects.requireNonNull(stored, "hash nullo");
            String[] parts = stored.split("\\$");
            if (parts.length != 4 || !parts[0].equals("pbkdf2_sha256")) {
                return false; // formato non riconosciuto
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = pbkdf2(plainPassword.toCharArray(), salt, iterations, expected.length * 8);
            // Confronto constant-time
            return constantTimeEquals(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    /** Facoltativo: policy di rehash (es. se aumenti le iterazioni). */
    public static boolean needsRehash(String stored, int desiredIterations) {
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4 || !parts[0].equals("pbkdf2_sha256")) return true;
            int iterations = Integer.parseInt(parts[1]);
            return iterations < desiredIterations;
        } catch (Exception e) {
            return true;
        }
    }

    // --------- helpers ---------

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
            byte[] dk = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return dk;
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 failure", e);
        }
    }

    private static byte[] randomBytes(int len) {
        byte[] out = new byte[len];
        RNG.nextBytes(out);
        return out;
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
