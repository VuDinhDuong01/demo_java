package com.example.demo.dtos.requests;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    @Data
    public static class CreatePermissionRequest {

        @NotEmpty(message = "Not empty")
        String name;

        String description;
    }

    @Data
    public static class UpdatePermissionRequest {

        @NotEmpty(message = "Not empty")
        String name;

        String description;

        @NotEmpty(message = "Not empty")
        UUID id;
    }

    @Data
    public static class DeletePermissionRequest {
        List<UUID> id;
    }

}
