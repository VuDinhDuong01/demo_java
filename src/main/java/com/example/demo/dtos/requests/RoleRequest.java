package com.example.demo.dtos.requests;

import java.util.Set;

import com.example.demo.utils.Enum.RoleType;
import com.example.demo.utils.validator.Role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @Data
    public static class CreateRoleRequest {
        
        @Role(name="name", enumRole=RoleType.class)
        String name;

        String description;
        Set<String> permissions;
    }
}
