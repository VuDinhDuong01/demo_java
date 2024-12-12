package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.CartRequest;
import com.example.demo.entity.CartEntity;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartEntity toCart(CartRequest cart);
}
