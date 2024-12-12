package com.example.demo.dtos.requests;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NotificationRequest {
     UUID userId;
    String message;
    Integer status;
    String type;
    UUID id;
}
