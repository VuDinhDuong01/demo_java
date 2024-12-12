package com.example.demo.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "review")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewProductEntity extends BaseEntity{
       @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(name ="user_id")
    UUID userId;

    @Column(name ="product_id")
    UUID productId;

    String review;
    Integer rating;

    List<String> Images;
}
