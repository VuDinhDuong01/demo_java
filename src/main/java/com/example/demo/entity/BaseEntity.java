package com.example.demo.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_by")
    UUID createdBy;

    @Column(name="updated_by")
    UUID updatedBy;

    @Column(name="created_at")
    Date createdAt;

    @Column(name="updated_at")
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
