package it.storeottana.vendita_prodotti.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.DecodingException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Component
public class TokenJWT {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long validityInMilliseconds;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Base64.getUrlDecoder().decode(secret.trim());
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException | DecodingException e) {
            // Se decodificare fallisce, assumiamo che il secret sia plain text
            this.key = Keys.hmacShaKeyFor(secret.trim().getBytes(StandardCharsets.UTF_8));
        }
    }

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        if (token == null) return "Token non trovato!";
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String tokenExpired(String token, String username) throws ExpiredJwtException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String usernameToken = claims.getSubject();
            if (usernameToken.equals(username)) {
                return "Token valido";
            }
        } catch (Exception e) {
            return "Token non valido";
        }
        return "Errore sconosciuto";
    }
    public String guestUsername () {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }
    public void addTokenToCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("guest777token", token)
                .path("/")
                .maxAge(60 * 60 * 24 * 6) // 6 giorni
                .secure(true)   // Il cookie sarà inviato solo su HTTPS
                .httpOnly(true) // Impedisce accesso JS
                .sameSite("None") // Permette l'invio cross-site
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
    public String getTokenFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = null;
        //Controlla l'esistenza del carrello
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guest777token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        return token;
    }

}