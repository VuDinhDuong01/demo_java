package com.example.demo.entity;
import java.util.UUID;

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

    String username;

    String phone;

    Integer status;
    String display;
    String message;
    String address;

}
