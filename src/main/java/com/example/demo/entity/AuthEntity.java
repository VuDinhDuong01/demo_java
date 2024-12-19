package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.util.UUID;
@Entity
@Data
@Table(name = "auth")
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AuthEntity extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @Column(name = "username" , nullable = false)
    String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    String password;

    
    @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(500) ")
    String email;
    
    String avatar;

    @JsonIgnore
    Integer verify = 0;

    List<String> signature;

    String role;

    @JsonIgnore
    @Column(name = "auth_provider")
    String authProvider;

   
    @Column(name = "is_delete")
    Boolean isDelete;

    @ManyToOne
    RoleEntity permissions;
}
