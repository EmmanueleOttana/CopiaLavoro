package it.storeottana.vendita_prodotti.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.DecodingException;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.repositories.AdminRepo;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Component
public class TokenJWT {

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private AdminRepo adminRepo;

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
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        Date expiration = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret) // Usa la tua chiave segreta
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, String username) {
        final String usernameR = extractUsername(token);
        Optional <Admin> adminR = adminRepo.findByUsername(usernameR);
        return adminR.filter(admin -> (usernameR.equals(username) && !isTokenExpired(token) &&
                admin.getToken() != null && admin.getTimestampToken() != null)).isPresent();
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
    public Optional<Admin> getAdminFromToken(String token){
        return adminRepo.findByUsername(extractUsername(token));
    }
    public boolean checkToken(Admin admin){
        if (admin.getToken() != null
        && !admin.getTimestampToken().isBefore(LocalDateTime.now().minusWeeks(1))){
            return true;
        }else {
            admin.setTimestampToken(null);
            admin.setToken(null);
            return false;
        }
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }


}