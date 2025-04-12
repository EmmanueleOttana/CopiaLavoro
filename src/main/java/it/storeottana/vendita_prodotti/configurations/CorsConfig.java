package it.storeottana.vendita_prodotti.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://127.0.0.1:5500",
                                "http://localhost:3000",
                                "http://www.storeottana.it",
                                "https://www.storeottana.it",
                                "http://storeottana.it",
                                "https://storeottana.it"
                        )
                        .allowedMethods("GET", "POST", "PATCH", "DELETE")
                        .allowedHeaders("*")
                        .exposedHeaders("BearerToken")
                        .allowCredentials(true); // Permetti credenziali, se necessario
            }
        };
    }
}