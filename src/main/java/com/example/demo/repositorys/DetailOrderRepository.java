package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.DetailOrderEntity;

@Repository
public interface DetailOrderRepository extends JpaRepository<DetailOrderEntity,UUID> {
     Page<DetailOrderEntity> findAll(Specification<DetailOrderEntity> spec, Pageable page);
} 
