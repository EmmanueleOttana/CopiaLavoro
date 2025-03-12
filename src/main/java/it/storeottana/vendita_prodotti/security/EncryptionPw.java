package it.storeottana.vendita_prodotti.security;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionPw {

        // Metodo per hashare la password
        public static String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        }

        // Metodo per verificare la password
        public static boolean checkPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
        }

}