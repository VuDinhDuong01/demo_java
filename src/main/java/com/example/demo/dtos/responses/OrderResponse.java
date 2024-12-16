package com.example.demo.dtos.responses;

import java.util.UUID;

import com.example.demo.entity.AuthEntity;
import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.ProductEntity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse extends BaseEntity{
    UUID id;
    UUID userId;
    String orderDate;
    Float price;
    String paymentMethod;
    String shipperAddress;
    Integer status;
    UUID productId;
    Integer quantity;
    Float priceSale;
    AuthEntity  userOrder;
    ProductEntity productOrder;
    AuthEntity userCreated;
    AuthEntity userUpdated;
}
