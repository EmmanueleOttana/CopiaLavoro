package it.storeottana.vendita_prodotti.configurations;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class SameSiteCookieFilter implements Filter {

    private static final String SAME_SITE_VALUE = "Strict";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        chain.doFilter(request, response);

        if (response instanceof HttpServletResponse) {
            HttpServletResponse res = (HttpServletResponse) response;

            Collection<String> headers = res.getHeaders("Set-Cookie");
            if (headers != null && !headers.isEmpty()) {
                res.setHeader("Set-Cookie", null); // Cancella header esistente
                for (String header : headers) {
                    // Se il cookie non contiene già la direttiva SameSite, la aggiungiamo.
                    StringBuilder newHeader = new StringBuilder(header);

                    // Controlla se il flag Secure è già presente, altrimenti aggiungilo in base all'ambiente
                    if (!header.toLowerCase().contains("secure")) {
                        newHeader.append("; Secure");
                    }
                    // Aggiungi SameSite se non presente
                    if (!header.toLowerCase().contains("samesite")) {
                        newHeader.append("; SameSite=").append("None");
                    }
                    // Aggiungi il cookie modificato all'header della risposta
                    res.addHeader("Set-Cookie", newHeader.toString());
                }
            }
        }
    }
}
