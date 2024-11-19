package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.dtos.requests.RoleRequest;
import com.example.demo.dtos.responses.RoleResponse;
import com.example.demo.entity.RoleEntity;

@Mapper(componentModel="spring")
public interface RoleMapper {
    @Mapping(target = "permissions",ignore = true)
    RoleEntity toRole(RoleRequest.CreateRoleRequest entity);

    RoleResponse.CreateRoleResponse toRoleResponse(RoleEntity role);
}
