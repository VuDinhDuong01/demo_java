package com.example.demo.dtos.responses;

import java.util.UUID;

import com.example.demo.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailOrderResponse extends BaseEntity{
    UUID id;
     UUID orderId;
    UUID productId;
    Integer quantity;
    Float totalPrice;
    Float utilPrice;
    Integer status;
}
