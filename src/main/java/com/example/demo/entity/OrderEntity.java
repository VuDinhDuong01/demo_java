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
@Table(name = "order")
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(name = "user_id")
    UUID userId;

    @Column(name = "order_date")
    String orderDate;

    @Column(name = "price")
    Float price;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "shipper_address")
    String shipperAddress;

    Integer status;
}
