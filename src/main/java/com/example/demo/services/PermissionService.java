package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.PermissionRequest;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.repositorys.PermissionRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PermissionService {

    private PermissionRepository permissionRepository;

    public String createPermission(PermissionRequest payload) {
        PermissionEntity checkPermissionExist = permissionRepository.findByName(payload.getName());
        if (checkPermissionExist != null) {
            throw new RuntimeException("permission existed");
        }

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(payload.getName());
        permissionEntity.setDescription(payload.getDescription());

        permissionRepository.save(permissionEntity);
        return "create permission success";

    }
}
