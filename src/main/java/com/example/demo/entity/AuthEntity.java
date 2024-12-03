package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "auth")
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthEntity extends BaseEntity {

    @Id
    String id;

    @Column(name = "username" , nullable = false)
    String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    String password;

    
    @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(500) ")
    String email;
    
    String avatar;

    @JsonIgnore
    Integer verify = 0;

    @JsonIgnore
    @Column(name = "verify_email")
    String verifyEmail;

    List<String> signature;

    @JsonIgnore
    @Column(name="forgot_password")
    String forgotPassword;

    String role;

    @JsonIgnore
    @Column(name = "auth_provider")
    String authProvider;

   
    @Column(name = "is_delete")
    Boolean isDelete = false;

    @ManyToOne
    RoleEntity permissions;
}
