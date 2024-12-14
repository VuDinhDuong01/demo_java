package com.example.demo.repositorys;


import java.util.Set;
import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PermissionEntity;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID>{
    PermissionEntity findByName(String name);
    
    Set<PermissionEntity> findByNameIn(Set<String> actions);

    Page<PermissionEntity> findAll(Specification<PermissionEntity> spec, Pageable paging );
} 
