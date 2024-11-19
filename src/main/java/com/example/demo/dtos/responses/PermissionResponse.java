package com.example.demo.dtos.responses;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class PermissionResponse {

    @Builder
    @Data
    public static class createPermissionResponse {
        String name;
        String description;
    }

}
