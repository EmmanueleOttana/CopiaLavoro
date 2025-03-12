package it.storeottana.vendita_prodotti.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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


}