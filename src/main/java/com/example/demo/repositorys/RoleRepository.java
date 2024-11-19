package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RoleEntity;

@Repository
public interface RoleRepository  extends JpaRepository<RoleEntity, UUID>{
    RoleEntity findByName(String name);
}