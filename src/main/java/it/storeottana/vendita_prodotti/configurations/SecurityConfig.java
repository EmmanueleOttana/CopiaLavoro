package it.storeottana.vendita_prodotti.configurations;

import it.storeottana.vendita_prodotti.security.JwtAuthenticationFilter;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private TokenJWT tokenJWT;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/registration", "/admin/activeAccount/*").permitAll()
                        .requestMatchers("/admin/*",
                                "/order/getAll",
                                "/order/cancel/*",
                                "/insertion/create",
                                "/insertion/update",
                                "/insertion/delete/*",
                                "/insertion/deleteAll").authenticated()
                        .anyRequest().permitAll()
                )
                // Registra il filtro JWT prima del filtro standard per l’autenticazione
                .addFilterBefore(new JwtAuthenticationFilter(tokenJWT), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:3000",
                                                    "http://www.storeottana.it", "https://www.storeottana.it",
                                                    "http://storeottana.it", "https://storeottana.it"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS", "PATCH", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("BearerToken"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
