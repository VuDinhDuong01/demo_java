package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ReviewProductEntity;

@Repository
public interface ReviewProductRepository  extends JpaRepository<ReviewProductEntity,UUID>{
    Page<ReviewProductEntity> findAll(Specification<ReviewProductEntity> spec, Pageable page);
}
