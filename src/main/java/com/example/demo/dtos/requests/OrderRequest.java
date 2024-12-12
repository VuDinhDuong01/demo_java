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

    String orderDate;
    Float price;
    UUID userId;
    UUID productId;
    Integer quantity;
    Integer status;
    String shipperAddress;
    String paymentMethod;
    UUID orderId;
}
