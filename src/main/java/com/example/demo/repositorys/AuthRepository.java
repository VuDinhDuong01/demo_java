package com.example.demo.repositorys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AuthEntity;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity,String> {
    AuthEntity findByEmail(String email);
    Page<AuthEntity> findAll(Specification<AuthEntity> spec, Pageable pageable);
    AuthEntity findByRole(String role);
} 