package com.example.demo.dtos.requests;

import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @Data
    public static class CreateRoleRequest {
        String name;
        String description;
        Set<String> permissions;
    }
}
