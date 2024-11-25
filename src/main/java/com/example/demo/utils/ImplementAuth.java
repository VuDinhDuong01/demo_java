package com.example.demo.utils;


import java.util.Map;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.dtos.requests.ForgotPasswordRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.LoginRequest;
import com.example.demo.dtos.requests.RegisterRequest;
import com.example.demo.dtos.requests.ResetPasswordRequest;
import com.example.demo.dtos.requests.UpdateUserRequest;
import com.example.demo.dtos.requests.VerifyTokenForgotPasswordRequest;
import com.example.demo.dtos.responses.AuthResponse;
import com.example.demo.entity.AuthEntity;

public interface ImplementAuth {


    UserDetailsService userDetailsService();

   AuthResponse.RegisterResponse register(RegisterRequest body);

     String verifyEmail(VerifyTokenForgotPasswordRequest payload);

   AuthResponse.LoginResponse login(LoginRequest payload);

 String generateToken(String secretKey, AuthEntity user);

    Map<String, Object> userFilter(GetAllRequest payload);

   AuthEntity updateUser(UpdateUserRequest payload);

   ForgotPasswordRequest forgotPassword(ForgotPasswordRequest payload);

   ForgotPasswordRequest verifyForgotPassword(VerifyTokenForgotPasswordRequest payload);
String resetPassword(ResetPasswordRequest payload);

}
