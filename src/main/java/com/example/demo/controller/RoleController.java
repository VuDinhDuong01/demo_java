package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.RoleRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.dtos.responses.RoleResponse;
import com.example.demo.services.RoleService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@Data
@RequestMapping("/api/v1")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    private RoleService roleService;
    
    @PostMapping("/role")
    // @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public BaseResponse<RoleResponse.CreateRoleResponse> createRole(@RequestBody @Valid RoleRequest.CreateRoleRequest body){
        RoleResponse.CreateRoleResponse response = roleService.createRole(body);
        return BaseResponse.<RoleResponse.CreateRoleResponse>builder().result(response).build();
    }
}
