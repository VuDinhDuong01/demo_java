package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.CategoryRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.CategoryEntity;
import com.example.demo.services.CategoryService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {

    CategoryService categoryService;

    @PostMapping("/category")
    public BaseResponse<CategoryEntity> create(@RequestBody @Valid CategoryRequest body) {

        CategoryEntity response = categoryService.create(body);

        return BaseResponse.<CategoryEntity>builder().result(response).build();
    }

    @PutMapping("/category")
    public BaseResponse<CategoryEntity> update(@RequestBody @Valid CategoryRequest body) {

        CategoryEntity response = categoryService.update(body);

        return BaseResponse.<CategoryEntity>builder().result(response).build();
    }

    @DeleteMapping("/category")
    public BaseResponse<String> delete(@RequestBody @Valid CategoryRequest body) {
        String response = categoryService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PostMapping("/filter-category")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body) {
        Map<String, Object> response = categoryService.getAllCategory(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
