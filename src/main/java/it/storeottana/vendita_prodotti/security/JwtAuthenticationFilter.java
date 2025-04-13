package it.storeottana.vendita_prodotti.security;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenJWT tokenJWT;

    public JwtAuthenticationFilter(TokenJWT tokenJWT) {
        this.tokenJWT = tokenJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String headerToken = request.getHeader("BearerToken");
        if (StringUtils.hasText(headerToken)) {
            try {
                String username = tokenJWT.getUsername(headerToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Errore nella validazione del token: " + e.getMessage());
            }
        } else {
            System.out.println("Nessun token trovato nell'header BearerToken");
        }

        filterChain.doFilter(request, response);
    }

}
