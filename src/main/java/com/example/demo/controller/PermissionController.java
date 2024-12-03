package com.example.demo.controller;



import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.PermissionRequest;
import com.example.demo.dtos.responses.BaseResponse;

import com.example.demo.services.PermissionService;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@SecurityRequirement(name ="bearerAuth")
@Tag(name = "PERMISSION")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("/permission")
  
    @Operation(
        description = "Post endpoint for permission",
        summary = "this is summery",
        responses = {
            @ApiResponse(
                description="SUCCESS",
                responseCode="200"
            )
        }
    )

    // @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public BaseResponse<String> createPermission(@RequestBody @Valid PermissionRequest.CreatePermissionRequest body) {
        
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt= (Jwt) auth.getPrincipal();
        String user_id= jwt.getClaim("user_id");
    
        System.out.println("auth:" +user_id);
        String response = permissionService.createPermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/permission")
    public  BaseResponse<String> updatePermission(@RequestBody @Valid PermissionRequest.UpdatePermissionRequest body) {
        String response = permissionService.updatePermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/permission")
    public  BaseResponse<String> deletePermission(@RequestBody @Valid PermissionRequest.DeletePermissionRequest body){
      
        String response = permissionService.deletePermission(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PostMapping("/permission-filter")
    public BaseResponse<Map<String, Object>> getAllPermission(@RequestBody @Valid GetAllRequest body){
        Map<String, Object> response = permissionService.getAllPermission(body);
        return BaseResponse.<Map<String, Object>>
        builder()
        .result(response)
        .build();
    }
}
