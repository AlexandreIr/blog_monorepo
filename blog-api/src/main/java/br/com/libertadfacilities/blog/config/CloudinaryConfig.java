package br.com.libertadfacilities.blog.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${Cloudinary.cloud_name}")
    private String cloudName;

    @Value("${Cloudinary.api_key}")
    private String apiKey;

    @Value("${Cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
