package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.ReviewProductRequest;
import com.example.demo.entity.ReviewProductEntity;

@Mapper(componentModel = "spring")
public interface ReviewProductMapper {
    ReviewProductEntity toReviewProduct(ReviewProductRequest review);
}
