package com.example.demo.repositorys;


import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PermissionEntity;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID>{
    PermissionEntity findByName(String name);
    Set<PermissionEntity> findAll(Set<String> name);
} 
