package com.example.demo.configs;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.SignatureAlgorithm;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("${spring.jwt.secretKey_access_token}")
        private String secretKey_access_token;
        
        private static final String[] PUBLIC_ROUTE = { "/api/v1/register", "/api/v1/login", "/api/v1/role"
        };

        @Bean
        public AuthenticationEntryPoint authenticationErrorConfig() {
                return new AuthenticationErrorConfig();
        }

        @Bean
        SecurityFilterChain appEndpoints(HttpSecurity http) throws Exception {
                // java default chặn mở chặn csrf;
                http
                                .csrf().disable()
                                .cors().disable()
                                .authorizeHttpRequests((authorize) -> authorize
                                                .requestMatchers(HttpMethod.POST,
                                                                PUBLIC_ROUTE)
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/*").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                // verify token, xác thực user. mỗi khi token truyền lên
                                                                // từ header.
                                                                .decoder(jwtDecoder())
                                                                // chuyển đổi thành đối tượng phục vụ cho quyền hạn
                                                                // user.
                                                                //  .jwtAuthenticationConverter(
                                                                // jwtAuthenticationConverter())
                                                ).authenticationEntryPoint(new AuthenticationErrorConfig()));

                return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder() {
                SecretKeySpec secretKeySpec = new SecretKeySpec(
                                "Gd7Jf9vZsPiZhvQ5l3X8mYvR6P8jTv1L2xQ6jYuTzWvR5dMfH2k7gQ==".getBytes(), 
                                SignatureAlgorithm.HS256.getJcaName());
                                
                return NimbusJwtDecoder
                                .withSecretKey(secretKeySpec)
                                .macAlgorithm(MacAlgorithm.HS256)
                                .build();
        };

        // @Bean
        // public JwtAuthenticationConverter jwtAuthenticationConverter() {
        //         JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        //         grantedAuthoritiesConverter.setAuthorityPrefix("");

        //         JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        //         jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        //         return jwtAuthenticationConverter;
        // }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }
}
