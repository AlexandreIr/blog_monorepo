package br.com.libertadfacilities.blog.config;

import br.com.libertadfacilities.blog.model.Role;
import br.com.libertadfacilities.blog.model.User;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdminIfNotExist(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){
        return args -> {

            String adminEmail = "liberadLCS@gmail.com";

            if(!userRepository.existsByEmail(adminEmail)){
                User admin = new User();
                admin.setName("Alexandre Silva");
                admin.setEmail(adminEmail);

                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);

                log.info("Admin user created: {}", userRepository.save(admin));
            } else {
                log.info("Admin user already exists");
            }
        };
    }
}
