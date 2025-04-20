package it.storeottana.vendita_prodotti.security;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenJWT tokenJWT;

    public JwtAuthenticationFilter(TokenJWT tokenJWT) {
        this.tokenJWT = tokenJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerToken = request.getHeader("BearerToken");

        if (StringUtils.hasText(headerToken)) {
            try {
                String username = tokenJWT.extractUsername(headerToken);
                if ( tokenJWT.isTokenValid(headerToken, username)
                        && SecurityContextHolder.getContext().getAuthentication() == null ) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Errore nella validazione del token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
