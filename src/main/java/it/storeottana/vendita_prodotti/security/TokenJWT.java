package it.storeottana.vendita_prodotti.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;

import java.util.Random;

@Configuration
public class TokenJWT {

    @Value("${JWT.Secret}")
    private String secret;

    public String getToken(String username) throws ExpiredJwtException {
         String token = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
         return token;
    }

    public String getUsername(String token) {
        if (token == null) return "";
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String isTokenExpired(String token, String username) throws ExpiredJwtException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            String usernameToken = claims.getSubject();
            if (usernameToken.equals(username)){return "Token valido";}
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