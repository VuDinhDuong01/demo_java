package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.CategoryRequest;
import com.example.demo.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toCategory(CategoryRequest category);
}
