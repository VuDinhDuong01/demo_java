package com.example.demo.configs;

import javax.crypto.spec.SecretKeySpec;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("${spring.jwt.secretKey_access_token}")
        private String secretKey_access_token;

        private static final String[] PUBLIC_ROUTE = { "/api/v1/register", "/api/v1/login" };
        private static final String[] PUBLIC_ROUTER_SWAGGER = { "/swagger-ui/**", "/v2/api-docs", "/v3/api-docs/**",
                        "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security",
                        "/configuration/webjars/**" };

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
                                                .requestMatchers(HttpMethod.POST, PUBLIC_ROUTE).permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/*").permitAll()
                                                .requestMatchers("/login", "/oauth2/**", "/api/v1/home","/custom-callback/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, PUBLIC_ROUTER_SWAGGER).permitAll()
                                                // .requestMatchers("/api/v1/oauth/**").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                // verify token, xác thực user. mỗi khi token truyền lên
                                                                // từ header.
                                                                .decoder(jwtDecoder())
                                                                // chuyển đổi thành đối tượng phục vụ cho quyền hạn
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter()))
                                                .authenticationEntryPoint(new AuthenticationErrorConfig()));
                http.oauth2Login()
                                .successHandler((request, response, authentication) -> {
                                        // Redirect về frontend URL sau khi login thành công
                                        response.sendRedirect("http://localhost:3000/user");
                                })
                                .failureHandler((request, response, exception) -> {
                                        // Redirect về trang thông báo lỗi của frontend
                                        response.sendRedirect("http://localhost:3000/login?error=true");
                                }).redirectionEndpoint().baseUri("/custom-callback/google");
                return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder() {
                SecretKeySpec secretKeySpec = new SecretKeySpec(
                                Decoders.BASE64.decode(secretKey_access_token),
                                SignatureAlgorithm.HS256.getJcaName());
                return NimbusJwtDecoder
                                .withSecretKey(secretKeySpec)
                                .macAlgorithm(MacAlgorithm.HS256)
                                .build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                // TỰ thêm chữ đứng đầu
                grantedAuthoritiesConverter.setAuthorityPrefix("");
                grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                return jwtAuthenticationConverter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }

        @Bean
        public XSSFWorkbook workbook() {
                return new XSSFWorkbook();
        }

        @Bean
        public XSSFSheet sheet(XSSFWorkbook workbook) {
                return workbook.createSheet("User");
        }
      
    
}
