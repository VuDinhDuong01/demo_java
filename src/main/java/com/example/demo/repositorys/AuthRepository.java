package com.example.demo.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AuthEntity;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, String> {
    AuthEntity findByEmail(String email);

    Optional<AuthEntity> findByUsername(String username);

    Page<AuthEntity> findAll(Specification<AuthEntity> spec, Pageable pageable);

    AuthEntity findByRole(String role);

    @Query("SELECT e FROM AuthEntity e WHERE e.username = :username AND e.role = :role")
    List<AuthEntity> findAuthEntity(@Param("username") String username, @Param("role") String role);
}