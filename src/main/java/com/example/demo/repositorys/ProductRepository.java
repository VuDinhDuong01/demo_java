package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
     ProductEntity findByName(String name);
    

    Page<ProductEntity>  findAll(Specification<ProductEntity> spec, Pageable page);
}
