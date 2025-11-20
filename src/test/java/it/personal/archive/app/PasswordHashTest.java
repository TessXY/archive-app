package it.personal.archive.app;

import it.personal.archive.app.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class PasswordHashTest {

    String plainPassword_giada = "giada";
    String plainPassword_alessio = "alessio";
    String plainPassword_admin = "admin";

    @Test
    public void checkHashedPassword() {
        // Hash passwords
        String hashed_giada = PasswordHasher.hash(plainPassword_giada);
        String hashed_alessio = PasswordHasher.hash(plainPassword_alessio);
        String hashed_admin = PasswordHasher.hash(plainPassword_admin);

        System.out.println("giada: " + hashed_giada);
        System.out.println("alessio: " + hashed_alessio);
        System.out.println("admin: " + hashed_admin);

        // 1. Verifica che un hash non sia uguale alla password originale
        assertNotEquals(plainPassword_giada, hashed_giada);
        assertNotEquals(plainPassword_alessio, hashed_alessio);
        assertNotEquals(plainPassword_admin, hashed_admin);

        // 2. Verifica che la verifica funzioni per password giuste
        assertTrue(PasswordHasher.verify(plainPassword_giada, hashed_giada));
        assertTrue(PasswordHasher.verify(plainPassword_alessio, hashed_alessio));
        assertTrue(PasswordHasher.verify(plainPassword_admin, hashed_admin));

        // 3. Verifica che la verifica fallisca con password sbagliate
        assertFalse(PasswordHasher.verify("sbagliata", hashed_giada));
        assertFalse(PasswordHasher.verify("pippo", hashed_alessio));
        assertFalse(PasswordHasher.verify("1234", hashed_admin));

        // 4. Verifica che hashing due volte la stessa password dia risultati diversi (salt random)
        String hashed_giada_2 = PasswordHasher.hash(plainPassword_giada);
        assertNotEquals(hashed_giada, hashed_giada_2);

        // 💡 5. Verifica che le password hashate abbiano un formato atteso
        assertTrue(hashed_giada.startsWith("pbkdf2_sha256$"));
        assertEquals(4, hashed_alessio.split("\\$").length);
    }



    // Esecuzione manuale per codifica e verifica
    public static void main(String[] args) {
        String plain = "giada";
        System.out.println("Password in chiaro: " + plain);

        // 1. Hashtag password
        String hashed = PasswordHasher.hash(plain);
        System.out.println("Hash generato: " + hashed);

        // 2. Decodifica/verifica
        boolean isMatch = PasswordHasher.verify(plain, hashed);
        System.out.println("Verifica corretta? " + isMatch);
    }



}
