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
public class ReviewProductRequest {
    
    UUID userId;
    UUID productId;
    String review="";
    List<String> Images= new ArrayList<>();
    UUID idReview;
    Integer rating;
}
