package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.ReviewProductRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.ReviewProductEntity;
import com.example.demo.services.BranchService;
import com.example.demo.services.ReviewProductService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewProductController {
    ReviewProductService reviewProductService;

    @PostMapping("/review-product")
    public BaseResponse<ReviewProductEntity> create(@RequestBody @Valid ReviewProductRequest body){

        ReviewProductEntity response = reviewProductService.create(body);

        return BaseResponse.<ReviewProductEntity>builder().result(response).build();
    }

    @PutMapping("/review-product")
    public BaseResponse<ReviewProductEntity> update(@RequestBody @Valid ReviewProductRequest body) {

        ReviewProductEntity response = reviewProductService.update(body);

        return BaseResponse.<ReviewProductEntity>builder().result(response).build();
    }


    @DeleteMapping("/review-product")
    public BaseResponse<String> delete(@RequestBody @Valid ReviewProductRequest body) {
        String response = reviewProductService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }


    @PostMapping("/filter-review")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body){

        Map<String , Object> response = reviewProductService.getAllReview(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
