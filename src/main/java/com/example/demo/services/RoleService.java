package com.example.demo.services;

import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.RoleRequest;
import com.example.demo.dtos.responses.RoleResponse;
import com.example.demo.entity.RoleEntity;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repositorys.PermissionRepository;
import com.example.demo.repositorys.RoleRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RoleService {
    
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;
    public RoleResponse.CreateRoleResponse createRole(RoleRequest.CreateRoleRequest payload){

        RoleEntity checkRoleExist = roleRepository.findByName(payload.getName());
        if (checkRoleExist != null) {
            throw new RuntimeException("role existed");
        }
        var role = roleMapper.toRole(payload);
        var permissions = permissionRepository.findAll(payload.getPermissions());

        role.setPermission(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(role);
    }
}
