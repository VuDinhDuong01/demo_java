package com.example.demo.dtos.requests;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    String email;
    String newPassword;
}
