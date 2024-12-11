package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CategoryEntity;
import com.example.demo.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Page<CategoryEntity> findAll(Specification<CategoryEntity> spec, Pageable page);
}
