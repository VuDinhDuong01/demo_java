package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID>{

    CategoryEntity findByName(String name);
    

    Page<CategoryEntity>  findAll(Specification<CategoryEntity> spec, Pageable page);
} 
