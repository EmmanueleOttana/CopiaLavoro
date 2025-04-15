package it.storeottana.vendita_prodotti.security;

import io.jsonwebtoken.ExpiredJwtException;
import it.storeottana.vendita_prodotti.entities.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenJWT jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(TokenJWT jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerToken = request.getHeader("BearerToken");
        String cookieToken = jwtService.getTokenFromCookie(request);

        if (headerToken != null) {
            try {
                String username = jwtService.extractUsername(headerToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(headerToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                // Token scaduto: non bloccare, prosegui senza autenticazione
                System.out.println("Token scaduto, richiesta non autenticata");
            } catch (Exception e) {
                System.out.println("Errore parsing JWT: " + e.getMessage());
            }
        }
        if (cookieToken == null) {
            String username = jwtService.guestUsername();
            Cart newCart = new Cart();
            newCart.setUsername(username);
            newCart.setToken(jwtService.createToken(username));

            jwtService.addTokenToCookie(response, newCart.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
