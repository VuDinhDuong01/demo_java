package com.example.demo.dtos.requests;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderRequest {
    String username;

    String phone;

    Integer status;
    String display;
    String message;
    String address;
    UUID id;
}
