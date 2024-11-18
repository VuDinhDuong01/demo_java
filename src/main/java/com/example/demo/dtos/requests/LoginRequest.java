package com.example.demo.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    
    @NotEmpty(message = "Trường này không được bỏ trống")
    String password;

    @NotEmpty(message = "Trường này không được bỏ trống")
    String email;
}
