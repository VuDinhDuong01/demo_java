package com.example.demo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dtos.requests.ExportRequest;
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

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Profile("dev")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;
    // MinioService minioService;

    @PostMapping("/register")
    public BaseResponse<AuthResponse.RegisterResponse> register(@RequestBody() @Valid RegisterRequest body) {
        AuthResponse.RegisterResponse response = authService.register(body);
        BaseResponse<AuthResponse.RegisterResponse> baseResponse = BaseResponse.<AuthResponse.RegisterResponse>builder()
                .result(response).build();
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
        BaseResponse<AuthResponse.LoginResponse> baseResponse = BaseResponse.<AuthResponse.LoginResponse>builder()
                .result(response)
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
    public BaseResponse<String> verifyTokenForgotPassword(
            @RequestBody VerifyTokenForgotPasswordRequest body) {
        String verifyForgotPasswordRequest = authService.verifyForgotPassword(body);
        BaseResponse<String> baseResponse = BaseResponse.<String>builder()
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

    @GetMapping("/home")
    public String homePage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            // Lấy thông tin người dùng từ Google
            System.out.println("User attributes: " + principal.getAttributes());
        } else {
            System.out.println("User not authenticated");
        }
        return "home"; // Chuyển đến trang home
    }

    @PostMapping("/import-user")
    public BaseResponse<AuthResponse.ImportUserResponse> importUser(@RequestParam("file") MultipartFile file) {
        AuthResponse.ImportUserResponse response = authService.importUser(file);
        BaseResponse<AuthResponse.ImportUserResponse> result = BaseResponse.<AuthResponse.ImportUserResponse>builder()
                .result(response).build();
        return result;
    }
    
    @PostMapping("/export-user-excel")
    public void exportUser(@RequestBody @Valid ExportRequest body, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=student" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        authService.generateExcelFile(body, response);

    }

    // @PostMapping(path = "/upload", consumes = {
    // MediaType.MULTIPART_FORM_DATA_VALUE })
    // public BaseResponse<List<Map<String, String>>> uploadFile(
    // @RequestPart(value = "file", required = true) List<MultipartFile> files) {
    // // List<Map<String, String>> response = minioService.uploadFile(files);

    // return BaseResponse.<List<Map<String,
    // String>>>builder().result(response).build();

    // }

    // @GetMapping(path = "/get-signature")
    // public BaseResponse<List<String>> getUrls() {
    // String userid = Utils.getUserId();
    // // List<String> response = minioService.getUrl(userid);
    // return BaseResponse.<List<String>>builder().result(response).build();
    // }

}
