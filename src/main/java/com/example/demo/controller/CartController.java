package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.CartRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.CartEntity;


import com.example.demo.services.CartService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level =AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
     CartService cartService;

    @PostMapping("/cart")
    public BaseResponse<CartEntity> create(@RequestBody @Valid CartRequest body){

        CartEntity response = cartService.create(body);

        return BaseResponse.<CartEntity>builder().result(response).build();
    }

    @PutMapping("/cart")
    public BaseResponse<CartEntity> update(@RequestBody @Valid CartRequest body) {

        CartEntity response = cartService.update(body);

        return BaseResponse.<CartEntity>builder().result(response).build();
    }


    @DeleteMapping("/cart")
    public BaseResponse<String> delete(@RequestBody @Valid CartRequest body) {
        String response = cartService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }


    @PostMapping("/filter-cart")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body){

        Map<String , Object> response = cartService.getAllCart(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
