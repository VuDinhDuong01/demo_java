package com.example.demo.configs;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.AuthEntity;
import com.example.demo.repositorys.AuthRepository;

@Configuration
public class AppConfig {
    @Bean
    CommandLineRunner initDatabase(AuthRepository authRepository, PasswordEncoder passwordEncoder) {

        return args -> {
            AuthEntity findAdminExisted = authRepository.findByRole("SUPER_ADMIN");
            if (findAdminExisted ==null ) {
                String hashPassowrd = passwordEncoder.encode("123456");
                AuthEntity authEntity = new AuthEntity();
                authEntity.setEmail("admin@gmail.com");
                authEntity.setVerify(1);
                authEntity.setPassword(hashPassowrd);
                authEntity.setUsername("SUPER_ADMIN");
                authEntity.setId(String.valueOf(UUID.randomUUID()));
                authEntity.setRole("SUPER_ADMIN");
                authRepository.save(authEntity);
            }
        };
    }
}
