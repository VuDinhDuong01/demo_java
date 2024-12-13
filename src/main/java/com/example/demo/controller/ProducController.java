package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.services.ProductService;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.ProductRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.ProductEntity;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProducController {
       ProductService productService;

    @PostMapping("/product")
    public BaseResponse<ProductEntity> create(@RequestBody @Valid ProductRequest body){

        ProductEntity response = productService.create(body);

        return BaseResponse.<ProductEntity>builder().result(response).build();
    }

    @PutMapping("/product")
    public BaseResponse<ProductEntity> update(@RequestBody @Valid ProductRequest body) {

        ProductEntity response = productService.update(body);

        return BaseResponse.<ProductEntity>builder().result(response).build();
    }


    @DeleteMapping("/product")
    public BaseResponse<String> delete(@RequestBody @Valid ProductRequest body) {
        String response = productService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }


    @PostMapping("/filter-product")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body){

        Map<String , Object> response = productService.getAllProduct(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
