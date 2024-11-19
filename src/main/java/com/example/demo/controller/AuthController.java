package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.ForgotPasswordRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.LoginRequest;
import com.example.demo.dtos.requests.RegisterRequest;
import com.example.demo.dtos.requests.ResetPasswordRequest;
import com.example.demo.dtos.requests.UpdateUserRequest;
import com.example.demo.dtos.requests.VerifyTokenForgotPasswordRequest;
import com.example.demo.dtos.responses.AuthResponse;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.AuthEntity;
import com.example.demo.services.AuthService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public BaseResponse<AuthResponse.RegisterResponse> register(@RequestBody() @Valid RegisterRequest body) {
        AuthResponse.RegisterResponse response = authService.register(body);
        BaseResponse<AuthResponse.RegisterResponse> baseResponse = BaseResponse.<AuthResponse.RegisterResponse>builder().result(response).build();
        return baseResponse;
    }

    @PostMapping("/verify-email")
    public BaseResponse<String> verifyEmail(@RequestBody @Valid VerifyTokenForgotPasswordRequest body) {
        String response = authService.verifyEmail(body);
        BaseResponse<String> baseResponse = BaseResponse.<String>builder().result(response).build();
        return baseResponse;
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponse.LoginResponse> login(@RequestBody @Valid LoginRequest body) {
        AuthResponse.LoginResponse response = authService.login(body);
        BaseResponse<AuthResponse.LoginResponse> baseResponse = BaseResponse.<AuthResponse.LoginResponse>builder().result(response)
                .build();
        return baseResponse;

    }

    @PostMapping("/user-filter")
    public BaseResponse<Map<String, Object>> userFilter(@RequestBody @Valid GetAllRequest body) {
        Map<String, Object> users = authService.userFilter(body);
        BaseResponse<Map<String, Object>> baseResponse = BaseResponse.<Map<String, Object>>builder()
                .result(users)
                .build();
        return baseResponse;
    }

    @PutMapping("/update-user")
    public BaseResponse<AuthEntity> updateUser(@RequestBody UpdateUserRequest body) {
        AuthEntity user = authService.updateUser(body);
        BaseResponse<AuthEntity> baseResponse = BaseResponse.<AuthEntity>builder()
                .result(user)
                .build();
        return baseResponse;
    }

    @PostMapping("/forgot-password")
    public BaseResponse<ForgotPasswordRequest> forgotPassword(@RequestBody ForgotPasswordRequest body) {

        ForgotPasswordRequest forgotPassword = authService.forgotPassword(body);
        BaseResponse<ForgotPasswordRequest> baseResponse = BaseResponse.<ForgotPasswordRequest>builder()
                .result(forgotPassword)
                .build();
        return baseResponse;
    }

    @PutMapping("/verify-token-forgot-password")
    public BaseResponse<ForgotPasswordRequest> verifyTokenForgotPassword(
            @RequestBody VerifyTokenForgotPasswordRequest body) {
        ForgotPasswordRequest verifyForgotPasswordRequest = authService.verifyForgotPassword(body);
        BaseResponse<ForgotPasswordRequest> baseResponse = BaseResponse.<ForgotPasswordRequest>builder()
                .result(verifyForgotPasswordRequest)
                .build();
        return baseResponse;
    }

    @PutMapping("/reset-password")
    public BaseResponse<String> resetPassword(
            @RequestBody ResetPasswordRequest body) {
        String resetPasswordResponse = authService.resetPassword(body);
        BaseResponse<String> baseResponse = BaseResponse.<String>builder()
                .result(resetPasswordResponse)
                .build();
        return baseResponse;
    }

}
