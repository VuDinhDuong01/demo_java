package com.example.demo.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthEntity {
    @Id
    String id;

    String username;
    String password;
    String email;

    String forgotPassword;

    String createdBy;
    String updatedBy;

    Date createdAt;
    Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt= new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
