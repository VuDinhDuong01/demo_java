package com.example.demo.dtos.responses;

import com.example.demo.entity.AuthEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {

    @Builder
    @Data
    public static class RegisterResponse {
        String id;
    }

    @Builder
    @Data
    public static class LoginResponse {
        AuthEntity data;
        Token token;
    }
}
