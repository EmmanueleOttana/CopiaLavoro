package it.storeottana.vendita_prodotti.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzaopwmcj",
                "api_key", "962472478436572",
                "api_secret", "NaXP3aTTAyctcfaRYHAWYhC3NmA"
        ));
    }


}
