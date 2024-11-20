package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    String username;

    @JsonIgnore
    String password;

    String email;
    
    String avatar;

    @JsonIgnore
    Integer verify = 0;

    @JsonIgnore
    String verify_email;

    @JsonIgnore
    String forgotPassword;

    String role;

    @ManyToOne
    RoleEntity permissions;
}
