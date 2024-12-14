package com.example.demo.repositorys;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BranchEntity;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, UUID> {
    BranchEntity findByName(String name);
    

    Page<BranchEntity>  findAll(Specification<BranchEntity> spec, Pageable page);
} 