package com.example.demo.dtos.responses;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportUser {
    String username;
    String role;
    List<String> listError;
}
