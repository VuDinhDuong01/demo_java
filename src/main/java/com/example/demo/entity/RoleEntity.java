package com.example.demo.entity;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    
    String name;
    String description;

    @ManyToMany
    Set<PermissionEntity> permission;
}
