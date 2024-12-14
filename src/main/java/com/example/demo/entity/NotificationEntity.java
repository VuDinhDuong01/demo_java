package com.example.demo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(name = "user_id")
    UUID userId;
    String message;
    Integer status;
    String type;
}
