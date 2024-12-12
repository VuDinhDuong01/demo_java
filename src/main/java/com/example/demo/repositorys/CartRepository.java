package com.example.demo.repositorys;

import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity,UUID>{
    Page<CartEntity> findAll(Specification<CartEntity> spec, Pageable page);
    
} 
