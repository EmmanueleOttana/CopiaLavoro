package it.storeottana.vendita_prodotti.security;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

class TokenJWTTest {

    @Test
    void getUsername() {
        for (int i = 0; i < 11; i++) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] secretBytes = new byte[36]; //36*8=288 (>256 bits required for HS256)
            secureRandom.nextBytes(secretBytes);
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            System.out.println(encoder.encodeToString(secretBytes));
        }

    }
}