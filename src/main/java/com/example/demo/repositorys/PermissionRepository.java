package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID>{
    PermissionEntity findByName(String name);
} 
