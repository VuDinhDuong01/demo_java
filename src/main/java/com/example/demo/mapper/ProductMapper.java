package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.ProductRequest;
import com.example.demo.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductEntity toProduct(ProductRequest product);
}
