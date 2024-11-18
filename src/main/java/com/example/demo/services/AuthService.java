package com.example.demo.services;

import java.security.Key;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.ForgotPasswordRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.LoginRequest;
import com.example.demo.dtos.requests.RegisterRequest;
import com.example.demo.dtos.requests.ResetPasswordRequest;
import com.example.demo.dtos.requests.UpdateUserRequest;
import com.example.demo.dtos.requests.VerifyTokenForgotPasswordRequest;
import com.example.demo.dtos.responses.LoginResponse;
import com.example.demo.dtos.responses.RegisterResponse;
import com.example.demo.dtos.responses.Token;
import com.example.demo.entity.AuthEntity;
import com.example.demo.repositorys.AuthRepository;
import com.example.demo.utils.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthService {
    @Value("${spring.jwt.secretKey_access_token}")
    private String secretKey_access_token;

    @Value("${spring.jwt.secretKey_refresh_token}")
    private String secretKey_refresh_token;

    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest body) {
        String subject = "Test email from Spring";
        try {
            emailService.sendEmail(body.getEmail(), subject);
            System.out.println("vào đây:");
        } catch (MessagingException e) {
            System.out.println("error send mail");
            e.printStackTrace();
        }
        AuthEntity findEmailExsist = authRepository.findByEmail(body.getEmail());
        if (findEmailExsist != null) {
            throw new RuntimeException("Tài khoản user đã tồn tại.");
        }
       
        String id = UUID.randomUUID().toString();

        String hashedPassword = passwordEncoder.encode(body.getPassword());
        AuthEntity authEntity = new AuthEntity();
        authEntity.setEmail(body.getEmail());
        authEntity.setPassword(hashedPassword);
        authEntity.setUsername(body.getUsername());
        authEntity.setId(id);
        authEntity.setCreatedBy(id);
        authRepository.save(authEntity);
        RegisterResponse registerResponse = RegisterResponse.builder().id(id).build();
        return registerResponse;

    }

    public LoginResponse login(LoginRequest payload) {

        AuthEntity findEmailUser = authRepository.findByEmail(payload.getEmail());

        if (findEmailUser == null) {
            throw new RuntimeException("user not exsist");
        }

        Boolean hashedPassword = passwordEncoder.matches(payload.getPassword(), findEmailUser.getPassword());
        if (!hashedPassword) {
            throw new RuntimeException("password not match.");
        }

        String access_token = this.generateToken(secretKey_access_token, findEmailUser);
        String refresh_token = this.generateToken(secretKey_refresh_token, findEmailUser);

        Token token = new Token();
        token.setAccess_token(access_token);
        token.setRefresh_token(refresh_token);

        System.out.println("response" + findEmailUser);

        LoginResponse loginResponse = LoginResponse.builder().data(findEmailUser).token(token)
                .build();

        return loginResponse;
    }

    private String generateToken(String secretKey, AuthEntity user) {
        String jwt = Jwts.builder()
                .setIssuer(user.getUsername())
                .setSubject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("scope", "admins")
                // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
                .setIssuedAt(new Date(System
                        .currentTimeMillis()))
                // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(
                        key(secretKey),
                        SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    private Key key(String secretKey) {
        System.out.println("secretKey:" + Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)));
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
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
                predicates.add(field.in(value));
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
            throw new RuntimeException("Không tìm thấy user.");
        }
        findUserUpdate.setUsername(payload.getUsername());
        findUserUpdate.setUpdatedAt(new Date());
        authRepository.save(findUserUpdate);
        return findUserUpdate;
    }

    public ForgotPasswordRequest forgotPassword(ForgotPasswordRequest payload) {
        String subject = Utils.randomToken();
        try {
            emailService.sendEmail(payload.getEmail(), subject);
        } catch (MessagingException e) {
            System.out.println("error send mail");
            e.printStackTrace();
        }
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
            throw new RuntimeException("Tài khoản không tồn tại.");
        }

        if (user.getForgotPassword() != payload.getToken()) {
            throw new RuntimeException("Mã xác thực không đúng");
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
            throw new RuntimeException("Tài khoản không tồn tại.");
        }

        AuthEntity authEntity = new AuthEntity();
        authEntity.setPassword(payload.getNewPassword());
        authRepository.save(authEntity);

        return "reset password success";
    }
}
