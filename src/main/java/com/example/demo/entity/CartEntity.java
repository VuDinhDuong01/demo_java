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
@Table(name = "cart")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartEntity extends BaseEntity{
    @Id
     @GeneratedValue(generator = "uuid")
    UUID id;
    @Column(name="product_id")
    UUID productId;
    @Column(name="user_id")

    UUID userId;

    Integer quantity;
}
