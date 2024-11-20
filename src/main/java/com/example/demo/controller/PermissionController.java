package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.PermissionRequest;
import com.example.demo.dtos.responses.BaseResponse;

import com.example.demo.services.PermissionService;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("/permission")
    public BaseResponse<String> createPermission(@RequestBody @Valid PermissionRequest.CreatePermissionRequest body) {
        String response = permissionService.createPermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PutMapping("/permission")
    public  BaseResponse<String> updatePermission(@RequestBody @Valid PermissionRequest.UpdatePermissionRequest body) {
        String response = permissionService.updatePermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @DeleteMapping("/permission")
    public  BaseResponse<String> deletePermission(@RequestBody @Valid PermissionRequest.DeletePermissionRequest body){
        String response = permissionService.deletePermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }
}
