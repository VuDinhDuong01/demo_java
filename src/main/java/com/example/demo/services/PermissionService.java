package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.PermissionRequest;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.repositorys.PermissionRepository;
import com.example.demo.utils.Utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PermissionService {

    private PermissionRepository permissionRepository;

    public String createPermission(PermissionRequest.CreatePermissionRequest payload) {
        PermissionEntity checkPermissionExist = permissionRepository.findByName(payload.getName());
        if (checkPermissionExist != null) {
            throw new RuntimeException("permission existed");
        }

        String user_id = Utils.getUserId();

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(payload.getName());
        permissionEntity.setDescription(payload.getDescription());
        permissionEntity.setCreatedBy(user_id);
        permissionRepository.save(permissionEntity);
        return "create permission success";
    }

    public String updatePermission(PermissionRequest.UpdatePermissionRequest payload) {

        PermissionEntity checkPermissionExist = permissionRepository.findById(payload.getId()).orElse(null);
        if (checkPermissionExist == null) {
            throw new RuntimeException("permission not found");
        }

        String user_id = Utils.getUserId();

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(payload.getName());
        permissionEntity.setDescription(payload.getDescription());
        permissionEntity.setUpdatedBy(user_id);
        permissionRepository.save(permissionEntity);
        return "update permission success";
    }

    public String deletePermission(PermissionRequest.DeletePermissionRequest payload) {
        List<PermissionEntity> permissions = permissionRepository.findAllById(payload.getId()).stream().toList();

        for (PermissionEntity per : permissions) {
            per.setIsDelete(true);
            permissionRepository.save(per);
        }
        return "delete permission success";
    }
}
