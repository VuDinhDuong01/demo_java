package com.example.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.services.AuthService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationConfig {
    private final PreFilter preFilter;
    // private final AuthService authService;

    final String[] WHITE_LIST = { "/api/v1/register", "/api/v1/login" };

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(WHITE_LIST).permitAll()
                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // authenticationProvider dùng để thông báo vowiss spring boot đây là cách mà
                // xác thực người dùng
                // khi người dùng đăng nhập
                // sử dụng userService.userDetailsService() để tìm thông tin người dùng từ data
                // sử dụng PasswordEncoder để kiểm tra mật khẩu.
                // nếu thông tin hợp lệ, người dùng xác thực thành công.
                // .authenticationProvider(provider())
                .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // private AuthenticationProvider provider() {
    //     DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //     provider.setUserDetailsService( k); // chỉ định lấy thông tin của user
    //     provider.setPasswordEncoder(getPasswordEncoder()); // chỉ định cách kiểm tra mật khẩu
    //     return provider;
    // }
    // @Bean
    // public PasswordEncoder getPasswordEncoder() {
    //     return new BCryptPasswordEncoder(10);
    // }
}
