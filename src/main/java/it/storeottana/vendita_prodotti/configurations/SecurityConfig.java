package it.storeottana.vendita_prodotti.configurations;

import it.storeottana.vendita_prodotti.security.JwtAuthenticationFilter;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private TokenJWT tokenJWT;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
            .addFilterBefore(new JwtAuthenticationFilter(tokenJWT), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:3000",
                "http://www.storeottana.it",
                "https://www.storeottana.it",
                "http://storeottana.it",
                "https://storeottana.it"
        ));
        config.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("BearerToken"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
