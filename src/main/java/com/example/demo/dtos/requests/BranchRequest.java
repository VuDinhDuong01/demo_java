package com.example.demo.dtos.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BranchRequest {
    
    String name;

    String slug="";

    List<String> images = new ArrayList<>();

    Integer status = 0;

    String metaTitle = "";

    String metaKeywords = "";

    UUID id;
}
