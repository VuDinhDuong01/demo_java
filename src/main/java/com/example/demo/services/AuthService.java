package com.example.demo.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.ForgotPasswordRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.LoginRequest;
import com.example.demo.dtos.requests.RegisterRequest;
import com.example.demo.dtos.requests.ResetPasswordRequest;
import com.example.demo.dtos.requests.UpdateUserRequest;
import com.example.demo.dtos.requests.VerifyTokenForgotPasswordRequest;
import com.example.demo.dtos.responses.AuthResponse;
import com.example.demo.dtos.responses.Token;
import com.example.demo.entity.AuthEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositorys.AuthRepository;
import com.example.demo.repositorys.RoleRepository;
import com.example.demo.utils.Utils;
import com.example.demo.utils.Enum.RoleType;
import com.example.demo.utils.Enum.TokenType;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.jwt.secretKey_access_token}")
    private String secretKey_access_token;

    @Value("${spring.jwt.secretKey_refresh_token}")
    String secretKey_refresh_token;

    @Autowired
    EmailService emailService;
    @Autowired
    AuthRepository authRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    KafkaTemplate kafkaTemplate;
    @Autowired
    JwtService jwtService;

    public AuthResponse.RegisterResponse register(RegisterRequest body) {
        AuthEntity findEmailExsist = authRepository.findByEmail(body.getEmail());
        if (findEmailExsist != null) {
            System.out.println("user not found:");
            throw new ForbiddenException("Tài khoản user đã tồn tại.");
        }
        String subject = "Token của bạn.";
        Context context = new Context();
        String html = "confirm-email.html";
        String token = Utils.randomToken();
        System.out.println("token:" + token);
        Map<String, Object> p = new HashMap<>();
        p.put("send-token", token);
        context.setVariables(p);

        // try {
        // emailService.sendEmail(body.getEmail(), subject, html, context);
        // } catch (MessagingException e) {
        // System.out.println("error send mail");
        // e.printStackTrace();
        // }
        kafkaTemplate.send("confirm-acount-topic", "duong2lophot@gmail.com");

        String id = UUID.randomUUID().toString();

        String hashedPassword = passwordEncoder.encode(body.getPassword());

        AuthEntity authEntity = new AuthEntity();
        authEntity.setRole(RoleType.USER.name());
        authEntity.setEmail(body.getEmail());
        authEntity.setVerifyEmail(token);
        authEntity.setAuthProvider("");
        authEntity.setPassword(hashedPassword);
        authEntity.setUsername(body.getUsername());
        authEntity.setId(id);
        authEntity.setCreatedBy(id);
        authRepository.save(authEntity);
        AuthResponse.RegisterResponse registerResponse = AuthResponse.RegisterResponse.builder().id(id).build();
        return registerResponse;
    }

    public String verifyEmail(VerifyTokenForgotPasswordRequest payload) {
        AuthEntity findEmailUser = authRepository.findByEmail(payload.getEmail());
        boolean checkVerify = Utils.checkVerifyTimer((java.sql.Date) findEmailUser.getCreatedAt());
        if (findEmailUser.getVerifyEmail() != payload.getToken()) {
            throw new ForbiddenException("Token của bạn không đúng");
        }
        if (!checkVerify) {
            throw new ForbiddenException("Token của bạn đã hết hạn");
        }
        AuthEntity authEntity = new AuthEntity();
        authEntity.setVerifyEmail("");
        authEntity.setVerify(1);

        authRepository.save(authEntity);
        return "verify email success";
    }

    public AuthResponse.LoginResponse login(LoginRequest payload) {

        AuthEntity findEmailUser = authRepository.findByEmail(payload.getEmail());

        if (findEmailUser == null) {
            throw new NotFoundException("user not exsist");
        }

        String checkVerify = findEmailUser.getVerifyEmail();

        // if (!checkVerify.equals("")) {
        // throw new RuntimeException("Bạn cần xác thực trước khi đăng nhập");
        // }

        Boolean hashedPassword = passwordEncoder.matches(payload.getPassword(), findEmailUser.getPassword());
        if (!hashedPassword) {
            throw new ForbiddenException("password not match.");
        }
        String access_token = jwtService.generateToken(TokenType.ACCESS_TOKEN, findEmailUser, 24*60*60*60*1000);
        String refresh_token = jwtService.generateToken(TokenType.REFRESH_TOKEN, findEmailUser, 360000);

        Token token = new Token();
        token.setAccess_token(access_token);
        token.setRefresh_token(refresh_token);

        RoleEntity getRoleDefault = roleRepository.findByName("USER");
        findEmailUser.setPermissions(getRoleDefault);
        findEmailUser.setRole(findEmailUser.getRole() == null ? "USER" : findEmailUser.getRole());
        AuthResponse.LoginResponse loginResponse = AuthResponse.LoginResponse.builder().data(findEmailUser).token(token)
                .build();

        return loginResponse;
    }

    public Map<String, Object> userFilter(GetAllRequest payload) {
        int page = payload.getStart() != null ? Integer.parseInt(payload.getStart()) : 0;
        int limit = payload.getLimit() != null ? Integer.parseInt(payload.getLimit()) : 10;

        String sortField = payload.getSortField() != null ? payload.getSortField() : "createdAt";
        String sortType = payload.getSortType() != null ? payload.getSortType() : "asc";

        Sort sort = sort(sortField, sortType);
        Pageable paging = PageRequest.of(page, limit, sort);
        List<Predicate> predicates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        Specification<AuthEntity> spec = (root, query, criteriaBuilder) -> {
            
            List<String> date = new ArrayList<>();
            date.add("createdAt");
            date.add("updateAt");
            predicates.add(criteriaBuilder.notEqual(root.get("isDelete"), true));
            for (ConditionRequest condition : payload.getConditions()) {
                Path<String> field = root.get(condition.getKey());
                List<String> value = condition.getValue();
                if (date.contains(condition.getKey())) {
                    if (value.size() >= 2) {
                        try {
                            if (value.size() >= 2) {
                                Date startDate = simpleDateFormat.parse(value.get(0));
                                Date endDate = simpleDateFormat.parse(value.get(1));
                                predicates.add(criteriaBuilder.between(field.as(Date.class), startDate, endDate));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if ("status".equals(condition.getKey())) {
                    System.out.println("status:");
                }
                predicates.add(criteriaBuilder.like(field, "%" + value.get(0).trim() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<AuthEntity> result = authRepository.findAll(spec, paging);
        List<Object> contents = new ArrayList();
        Map<String, Object> mapResult = new HashMap<>();
        contents.add(result.getContent());
        mapResult.put("results", result.getContent().size() > 0 ? contents : new ArrayList());
        mapResult.put("current", result.getPageable().getPageNumber());
        mapResult.put("totalPage", result.getTotalPages());
        mapResult.put("totalElement", result.getTotalElements());
        return mapResult;
    }

    private Sort sort(String sortField, String sortType) {
        return sortType.equals("asc") ? Sort.by(Sort.Direction.ASC, sortField)
                : Sort.by(Sort.Direction.DESC, sortField);
    }

    public AuthEntity updateUser(UpdateUserRequest payload) {

        AuthEntity findUserUpdate = authRepository.findById(payload.getUserIdUpdate()).orElse(null);
        if (findUserUpdate == null) {
            throw new NotFoundException("Không tìm thấy user.");
        }
        findUserUpdate.setUsername(payload.getUsername());
        findUserUpdate.setUpdatedAt(new Date());
        authRepository.save(findUserUpdate);
        return findUserUpdate;
    }

    public ForgotPasswordRequest forgotPassword(ForgotPasswordRequest payload) {
        String subject = "Testing send mail reset password";
        String token = Utils.randomToken();
        String html = "send-mail-reset-password.html";
        Context context = new Context();
        Map<String, Object> field = new HashMap<>();
        field.put("field", token);
        context.setVariables(field);
        // try {
        // emailService.sendEmail(payload.getEmail(), subject, html, context);
        // } catch (MessagingException e) {
        // System.out.println("error send mail");
        // e.printStackTrace();
        // }
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        AuthEntity authEntity = new AuthEntity();
        authEntity.setForgotPassword(subject);
        forgotPasswordRequest.setEmail(payload.getEmail());
        authRepository.save(authEntity);
        return forgotPasswordRequest;
    }

    public ForgotPasswordRequest verifyForgotPassword(VerifyTokenForgotPasswordRequest payload) {
        AuthEntity user = authRepository.findByEmail(payload.getEmail());
        if (user == null) {
            throw new NotFoundException("Tài khoản không tồn tại.");
        }

        if (user.getForgotPassword() != payload.getToken()) {
            throw new ForbiddenException("Mã xác thực không đúng");
        }

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(payload.getEmail());
        AuthEntity authEntity = new AuthEntity();
        authEntity.setForgotPassword("");
        return forgotPasswordRequest;
    }

    public String resetPassword(ResetPasswordRequest payload) {
        AuthEntity user = authRepository.findByEmail(payload.getEmail());
        if (user == null) {
            throw new NotFoundException("Tài khoản không tồn tại.");
        }

        AuthEntity authEntity = new AuthEntity();
        authEntity.setPassword(payload.getNewPassword());
        authRepository.save(authEntity);

        return "reset password success";
    }

    public 
}
