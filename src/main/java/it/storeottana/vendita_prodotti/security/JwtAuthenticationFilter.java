package it.storeottana.vendita_prodotti.security;

import it.storeottana.vendita_prodotti.entities.Cart;
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
        String cookieToken = tokenJWT.getTokenFromCookie(request);

        if (StringUtils.hasText(headerToken)) {
            try {
                String username = tokenJWT.extractUsername(headerToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Errore nella validazione del token: " + e.getMessage());
            }
        }
        if (cookieToken == null) {
            String username = tokenJWT.guestUsername();
            Cart newCart = new Cart();
            newCart.setUsername(username);
            newCart.setToken(tokenJWT.createToken(username));

            tokenJWT.addTokenToCookie(response, newCart.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
