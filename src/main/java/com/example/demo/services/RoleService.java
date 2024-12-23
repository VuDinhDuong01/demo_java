package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.RoleRequest;
import com.example.demo.dtos.responses.RoleResponse;
import com.example.demo.entity.RoleEntity;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repositorys.PermissionRepository;
import com.example.demo.repositorys.RoleRepository;
import com.example.demo.utils.Utils;

import java.util.HashSet;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse.CreateRoleResponse createRole(RoleRequest.CreateRoleRequest payload) {

        RoleEntity checkRoleExist = roleRepository.findByName(payload.getName());
        if (checkRoleExist != null) {
            throw new ForbiddenException("role existed");
        }

        String user_id = Utils.getUserId();
        var role = roleMapper.toRole(payload);
        var permissions = permissionRepository.findByNameIn(payload.getPermissions());
        role.setPermissions(permissions);
        role.setCreatedBy(UUID.fromString(user_id));
        roleRepository.save(role);
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(role);
    }
}
