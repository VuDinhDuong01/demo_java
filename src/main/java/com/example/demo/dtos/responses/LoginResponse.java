package com.example.demo.dtos.responses;

import com.example.demo.entity.AuthEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponse {
    AuthEntity data;
    Token token;
}


