package it.storeottana.vendita_prodotti.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // tutte le rotte
                        .allowedOrigins(
                                "http://127.0.0.1:5500",
                                "http://localhost:3000",
                                "http://www.storeottana.it",
                                "https://www.storeottana.it",
                                "http://storeottana.it",
                                "https://storeottana.it"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // specifica i metodi che usi
                        .allowedHeaders("*"); // consenti tutti gli headers
            }
        };
    }
}