package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.ExportRequest;
import com.example.demo.dtos.requests.ForgotPasswordRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.LoginRequest;
import com.example.demo.dtos.requests.RegisterRequest;
import com.example.demo.dtos.requests.ResetPasswordRequest;
import com.example.demo.dtos.requests.UpdateUserRequest;
import com.example.demo.dtos.requests.VerifyTokenForgotPasswordRequest;
import com.example.demo.dtos.responses.AuthResponse;
import com.example.demo.dtos.responses.ImportUser;
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
import com.example.demo.utils.validator.ValidateImportUser;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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

  @Autowired
  XSSFWorkbook workbook;

  @Autowired
  XSSFSheet sheet;

  @Autowired
  RedisService redisService;

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

    String hashedPassword = passwordEncoder.encode(body.getPassword());
    // save opt to redis

    redisService.SaveOTP(body.getEmail() + "_register", token);
   

    AuthEntity authEntity = new AuthEntity();
    authEntity.setRole(RoleType.USER.name());
    authEntity.setEmail(body.getEmail());
    authEntity.setAuthProvider("");
    authEntity.setPassword(hashedPassword);
    authEntity.setUsername(body.getUsername());
    authRepository.save(authEntity);
    AuthResponse.RegisterResponse registerResponse = AuthResponse.RegisterResponse.builder().email(body.getEmail()).build();
    return registerResponse;
  }

  public String verifyEmail(VerifyTokenForgotPasswordRequest payload) {

    // get OTP from redis
    String getOtpFormRedis = redisService.getOTP(payload.getEmail() + "_register");
    System.out.println("getOtpFormRedis:" +  getOtpFormRedis);
    System.out.println("payload.getToken():" +  payload.getToken());
    if (getOtpFormRedis == null) {
      throw new ForbiddenException("Token của bạn đã hết hạn");
    }
 
    if (!getOtpFormRedis.equals(payload.getToken())) {
      throw new ForbiddenException("Token của bạn không đúng");
    }
  
    redisService.deleteOTP(payload.getEmail());
    AuthEntity authEntity = new AuthEntity();
    authEntity.setVerify(1);
    authRepository.save((authEntity));
    return "verify email success";
  }

  public AuthResponse.LoginResponse login(LoginRequest payload) {

    AuthEntity findEmailUser = authRepository.findByEmail(payload.getEmail());

    if (findEmailUser == null) {
      throw new NotFoundException("user not exsist");
    }

    if (findEmailUser.getVerify() == 0) {
      throw new NotFoundException("Tài khoản của bạn chưa được xác thực để sử dụng.");
    }

    Boolean hashedPassword = passwordEncoder.matches(payload.getPassword(), findEmailUser.getPassword());
    if (!hashedPassword) {
      throw new ForbiddenException("password not match.");
    }
    String access_token = jwtService.generateToken(TokenType.ACCESS_TOKEN, findEmailUser, 24 * 60 * 60 * 60 * 1000);
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

    AuthEntity findUserUpdate = authRepository.findById(UUID).orElse(null);
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

    // save otp to reids
    // redisService.SaveOTP(payload.getEmail() + "_forgot_password", token);

    forgotPasswordRequest.setEmail(payload.getEmail());
    authRepository.save(authEntity);
    return forgotPasswordRequest;
  }

  public String verifyForgotPassword(VerifyTokenForgotPasswordRequest payload) {
    AuthEntity user = authRepository.findByEmail(payload.getEmail());
    if (user == null) {
      throw new NotFoundException("Tài khoản không tồn tại.");
    }

    // get OTP from Redis
  //   String getOtpFormRedis = redisService.getOTP(payload.getEmail() + "_forgot_password");

  //   if (getOtpFormRedis == null) {
  //     throw new ForbiddenException("Token của bạn đã hết hạn");
  //   }

  //   if (getOtpFormRedis != null && getOtpFormRedis != payload.getToken()) {
  //     throw new ForbiddenException("Token của bạn không đúng");
  //   }

  //   redisService.deleteOTP(payload.getEmail());
    return payload.getEmail();
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

  public AuthResponse.ImportUserResponse importUser(MultipartFile file) {

    try {
      InputStream inputStreamFile = file.getInputStream();
      Workbook workbook = new XSSFWorkbook(inputStreamFile);
      Sheet sheet = workbook.getSheetAt(0);
      Row excelHeader = sheet.getRow(0);
      Map<String, Integer> headerMapValue = new HashMap<>();
      List<ImportUser> success = new ArrayList<>();
      List<ImportUser> error = new ArrayList<>();

      for (int i = 0; i < excelHeader.getLastCellNum(); i++) {
        if (excelHeader.getCell(i).getCellType() == CellType.NUMERIC) {
          continue;
        }
        headerMapValue.put(excelHeader.getCell(i).getStringCellValue(), excelHeader.getCell(i).getColumnIndex());
      }

      if (!checkFieldHeader(excelHeader, headerMapValue)) {
        throw new ForbiddenException("Bạn chưa nhập đủ các trường.");
      }

      Integer indexUsername = headerMapValue.get("Tên");
      Integer indexRole = headerMapValue.get("Vai trò");

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        ImportUser importUser = new ImportUser();
        Row row = sheet.getRow(i);

        String username = row.getCell(indexUsername) != null ? row.getCell(indexUsername).getStringCellValue() : null;
        String role = row.getCell(indexRole) != null ? row.getCell(indexRole).getStringCellValue() : null;

        importUser.setUsername(username);
        importUser.setRole(role);

        List<String> validateRow = ValidateImportUser.validateRow(row, headerMapValue);
        if (validateRow.size() > 0) {
          importUser.setListError(validateRow);
          error.add(importUser);
          continue;
        }

        importUser.setListError(null);
        success.add(importUser);
      }

      AuthResponse.ImportUserResponse response = AuthResponse.ImportUserResponse
          .builder()
          .success(success)
          .error(error)
          .build();

      workbook.close();
      return response;

    } catch (IOException e) {
      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
    }
  }

  private Boolean checkFieldHeader(Row excelHeader, Map<String, Integer> headerMapValue) {
    Set<String> HeaderTitle = new HashSet<>();
    HeaderTitle.add("Vai trò");
    HeaderTitle.add("Tên");
    return headerMapValue.keySet().containsAll(HeaderTitle);
  }

  private void writeHeader() {
    Row row = sheet.createRow(0);
    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(16);

    createCell(row, 0, "ID", style);
    createCell(row, 1, "username", style);
    createCell(row, 2, "email", style);
    createCell(row, 3, "role", style);

  }

  private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
    sheet.autoSizeColumn(columnCount);
    Cell cell = row.createCell(columnCount);
    if (valueOfCell instanceof Integer) {
      cell.setCellValue((Integer) valueOfCell);
    } else if (valueOfCell instanceof Long) {
      cell.setCellValue((Long) valueOfCell);
    } else if (valueOfCell instanceof String) {
      cell.setCellValue((String) valueOfCell);
    } else {
      cell.setCellValue((Boolean) valueOfCell);
    }
    cell.setCellStyle(style);
  }

  private void write(List<AuthEntity> users) {
    int rowCount = 1;
    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(14);
    style.setFont(font);

    for (AuthEntity user : users) {
      Row row = sheet.createRow(rowCount++);
      int columnCount = 0;
      createCell(row, columnCount++, user.getId(), style);
      createCell(row, columnCount++, user.getUsername(), style);
      createCell(row, columnCount++, user.getEmail(), style);
      createCell(row, columnCount++, user.getRole(), style);
    }
  }

  public void generateExcelFile(ExportRequest payload, HttpServletResponse response) {
    List<AuthEntity> users = authRepository.findAuthEntity(payload.getUsername(), payload.getRole());
    writeHeader();
    write(users);
    try (ServletOutputStream outputStream = response.getOutputStream();) {
      workbook.write(outputStream);
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
