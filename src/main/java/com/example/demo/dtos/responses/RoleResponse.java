package com.example.demo.dtos.responses;

import java.util.Set;

import com.example.demo.entity.PermissionEntity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    @Data
    public static class CreateRoleResponse {
        String name;
        String description;
        Set<PermissionEntity> permissions;
    }
}
