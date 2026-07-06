package br.com.libertadfacilites.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.servlet.autoconfigure.MultipartProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultipartDebugConfig {

    private final MultipartProperties multipartProperties;

    @PostConstruct
    public void logMultipartConfig() {
        System.out.println("Multipart max-file-size: " + multipartProperties.getMaxFileSize());
        System.out.println("Multipart max-request-size: " + multipartProperties.getMaxRequestSize());
    }
}