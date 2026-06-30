package br.com.libertadfacilities.blog.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultipartDebugConfig {

    @Bean
    public CommandLineRunner multipartDebugRunner(MultipartConfigElement multipartConfigElement) {
        return args -> {
            System.out.println("========== MULTIPART CONFIG ATIVA ==========");
            System.out.println("Location: " + multipartConfigElement.getLocation());
            System.out.println("Max file size: " + multipartConfigElement.getMaxFileSize() + " bytes");
            System.out.println("Max request size: " + multipartConfigElement.getMaxRequestSize() + " bytes");
            System.out.println("File size threshold: " + multipartConfigElement.getFileSizeThreshold() + " bytes");
            System.out.println("============================================");
        };
    }
}