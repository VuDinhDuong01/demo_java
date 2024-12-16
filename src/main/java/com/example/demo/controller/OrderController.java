package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.OrderRequest;

import com.example.demo.entity.OrderEntity;
import com.example.demo.services.OrderService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.dtos.responses.OrderResponse;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderController {
    OrderService orderService;

    @PostMapping("/order")
    public BaseResponse<OrderResponse> create(@RequestBody @Valid OrderRequest body) {
        OrderResponse response = orderService.create(body);
        return BaseResponse.<OrderResponse>builder().result(response).build();
    }

    @PutMapping("/order")
    public BaseResponse<OrderResponse> update(@RequestBody @Valid OrderRequest body) {
        OrderResponse response = orderService.update(body);
        return BaseResponse.<OrderResponse>builder().result(response).build();
    }

    @DeleteMapping("/order")
    public BaseResponse<String> delete(@RequestBody @Valid OrderRequest body) {
        String response = orderService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }

    @PostMapping("/filter-order")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body) {
        Map<String, Object> response = orderService.getAllOrder(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }

    @GetMapping("/order")
    public BaseResponse<List<OrderEntity>> findOrderByUser() {
        List<OrderEntity> response = orderService.getOrderByUser();
        return BaseResponse.<List<OrderEntity>>builder().result(response).build();
    }
}
