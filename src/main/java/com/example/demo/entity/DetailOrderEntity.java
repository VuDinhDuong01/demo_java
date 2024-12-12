package com.example.demo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order-detail")
public class DetailOrderEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "product_id")
    UUID productId;

    Integer quantity;

    @Column(name = "total_price")
    Float totalPrice;

    @Column(name = "util_price")
    Float utilPrice;

    Integer status;
}
