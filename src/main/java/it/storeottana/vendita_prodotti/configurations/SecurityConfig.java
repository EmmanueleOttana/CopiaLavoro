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

@Configuration
public class SecurityConfig {

    @Autowired
    private TokenJWT tokenJWT;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
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


}
