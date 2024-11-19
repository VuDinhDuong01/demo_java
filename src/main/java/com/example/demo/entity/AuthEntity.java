package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    String password;
    String email;
    String avatar;

    Integer verify =0;

    String verify_email;

    String forgotPassword; 
}
